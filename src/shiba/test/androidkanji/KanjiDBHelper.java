package shiba.test.androidkanji;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.SQLException;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

public class KanjiDBHelper extends SQLiteOpenHelper {
	// TODO : rework the APP_NAME stuff in order to have the application name in only one location
	public static final String APP_NAME = "AndroidKanji";
	
	public static final String KEY_ID = "_id";
	public static final String KEY_GRADE = "grade";
	public static final String KEY_STROKE_COUNT = "strokeCount";
	public static final String KEY_FREQUENCY = "frequency";
	public static final String KEY_JLPT = "jlpt";
	public static final String KEY_PATH = "path";
	public static final String TABLE_ENTRIES = "entries";
	public static final String TABLE_FAVORITES = "favorites";
	public static final int KANJI_FILTER_ALL = 0;
	public static final int KANJI_FILTER_N1 = 1;
	public static final int KANJI_FILTER_N2 = 2;
	public static final int KANJI_FILTER_N3 = 3;
	public static final int KANJI_FILTER_N4 = 4;
	public static final int KANJI_FILTER_N5 = 5;
	public static final int KANJI_FILTER_FAVORITES = 6;
	
	private static String DB_NAME = "kanjidic2-en.db";
	private final String DBVERSION_FILE = "dbversion.txt";
	private static String FAVORITES_BACKUP_FILE = "favbkp.txt";
	private final int FIRST_KANJI_CODE = 19968;
	private final int LAST_KANJI_CODE = 40907;
	private static final String DB_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +APP_NAME +File.separator;
	private static final int DB_VERSION = 5;
	private final String TABLE_INFOS = "info";
	private final String KEY_VERSION = "version";
	
	private final Context mCtx;
	private SQLiteDatabase mDb;
	
	
	public KanjiDBHelper(Context ctx){
		super(ctx, DB_NAME, null, DB_VERSION);
		mCtx = ctx;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
	public void initDB() throws IOException{
        try{
        	// If the database is not created, create it
        	boolean dbExists = checkDatabase();
        	boolean createDB = false;
        	boolean updatedDB = false;
        	if(dbExists){
        		// The DB already exists. Should we update it?
        		int installedVersion = dbVersion();
        		
        		System.out.println("installed db version: " +installedVersion);
        		System.out.println("package db version: " +packageDBVersion());
        		
        		if(installedVersion < packageDBVersion()){
        			// 	The DB located in the package has changed. We must update the installed DB
        			backupFavorites();
        			deleteDB();
        			createDB = true;
        			updatedDB = true;
        		}
        		
        	}else{
        		createDB = true;
        	}
        	
        	if(createDB){
        		createDatabase();
        	}
        	if(updatedDB){
        		restoreBackupedFavorites();
        	}
        	
        }catch(IOException e){
        	throw new Error("Unable to create database!");
        }
	}
	
	private void backupFavorites() throws IOException{
		SQLiteDatabase db = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READONLY);
		
		String query = "SELECT " +KEY_ID +" FROM " +TABLE_FAVORITES;
		Cursor c = db.rawQuery(query, null);
		int index = c.getColumnIndex(KEY_ID);
		
		// Create the backup file
		String outFileName = DB_PATH + FAVORITES_BACKUP_FILE;
		OutputStream output = new FileOutputStream(outFileName);
		
		byte[] buffer = new byte[1024];
		
		for(int i=0; i< c.getCount(); i++){
			c.moveToPosition(i);
			// Add a line with the current favorite in the backup file
			String value = String.valueOf(c.getInt(index)) +"\r\n";
			buffer = value.getBytes();
			output.write(buffer);
		}
		
		output.flush();
		output.close();
		
		db.close();
	}
	
	private void deleteDB(){
		File dbFile = new File(DB_PATH + DB_NAME);
		dbFile.delete();
		
	}
	
	private void restoreBackupedFavorites() throws IOException{
		SQLiteDatabase db = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READWRITE);
		
		File myFile = new File(DB_PATH + FAVORITES_BACKUP_FILE);
		FileInputStream fIn = new FileInputStream(myFile);
		BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
		String strLine = "";
		while ((strLine = myReader.readLine()) != null) {
			db.rawQuery("INSERT INTO " +TABLE_FAVORITES +" (" +KEY_ID +") VALUES(" +Integer.parseInt(strLine) +")", null);
		}
		myReader.close();
		db.close();
		File favoriteBkp = new File(DB_PATH + FAVORITES_BACKUP_FILE);
		favoriteBkp.delete();
	}
	
	public int dbVersion(){
		int version = 0;
		SQLiteDatabase installedDB = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READONLY);
		
		String query = "SELECT " +KEY_VERSION +" FROM " +TABLE_INFOS;
		Cursor c = installedDB.rawQuery(query, null);
		c.moveToFirst();
		version = c.getInt(0);
		
		installedDB.close();
		return version;
	}
	
	private int packageDBVersion() throws IOException{
		int version = 0;
		
		InputStream input = mCtx.getAssets().open(DBVERSION_FILE);
		DataInputStream in = new DataInputStream(input);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
	  
		String strLine;
		while ((strLine = br.readLine()) != null){
			version = Integer.parseInt(strLine);
		}
		in.close();
		input.close();
		
		return version;
	}
	
	public void createDatabase() throws IOException{
		this.getReadableDatabase();
		try{
			copyDatabase();
		}catch(IOException e){
			throw new Error("Error copying database");
		}
		this.close();
	}
	
	private boolean checkDatabase(){
		
		SQLiteDatabase checkDB = null;
		try{
			// We try to open the DB
			checkDB = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READONLY);
		}catch(SQLiteException e){
			// Falling here means that the DB doesn't exist
			return false;
		}
		// Falling here means that the DB exists, as we will not use it, we close it.
		checkDB.close();
		return true;
	}
	
	private void copyDatabase() throws IOException{
		InputStream input = mCtx.getAssets().open(DB_NAME);
		String outFileName = DB_PATH + DB_NAME;
		OutputStream output = new FileOutputStream(outFileName);
		
		byte[] buffer = new byte[1024];
		int length;
		while((length = input.read(buffer)) > 0){
			output.write(buffer, 0, length);
		}
		
		output.flush();
		output.close();
		input.close();
	}
	
	public void openDatabase() throws SQLException{
		mDb = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READONLY);
	}
	
	public synchronized void close(){
		if(mDb != null){
			mDb.close();
		}
		super.close();
	}
	
	public String fetchAllKanji(){
		String query = "SELECT " + KEY_ID +" FROM " + TABLE_ENTRIES + " WHERE " + KEY_ID + ">=" +FIRST_KANJI_CODE +" AND " +KEY_ID +"<=" +LAST_KANJI_CODE;
		return query;
	}
	
	public String fetchFavoritesKanji(){
		String query = "";
		return query;
	}
	
	public Cursor fetchKanji(int group){
		String query;
		switch(group){
			case KANJI_FILTER_ALL:
				query = fetchAllKanji();
				break;
			case KANJI_FILTER_FAVORITES:	
				query = fetchFavoritesKanji();
				break;
			default:
				query = "SELECT " + KEY_ID + " FROM " + TABLE_ENTRIES + " WHERE " + KEY_JLPT + " IS " + group +" ORDER BY " +KEY_STROKE_COUNT +" ASC";
				break;
		}
		Cursor c = mDb.rawQuery(query, null);
		c.moveToFirst();
		return c;
	}
	
	public Cursor fetchKanji(long rowId){
		Cursor mCursor =

            mDb.query(true, TABLE_ENTRIES, new String[] {KEY_ID,
                    KEY_GRADE, KEY_STROKE_COUNT, KEY_FREQUENCY, KEY_JLPT}, KEY_ID + "=" + rowId, null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
	}
	
	boolean isInFavorites(int codePoint){
		String query = "SELECT " +KEY_ID +" FROM " + TABLE_FAVORITES +" WHERE " +KEY_ID +" IS " +codePoint;
		Cursor c = mDb.rawQuery(query, null);
		
		if(0 == c.getCount()){
			return false;
		}else{
			return true;
		}
	}
}
