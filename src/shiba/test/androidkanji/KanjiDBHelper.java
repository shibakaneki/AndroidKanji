package shiba.test.androidkanji;

import java.io.BufferedReader;
import java.io.File;
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
	public static final int KANJI_FILTER_ALL = 0;
	public static final int KANJI_FILTER_N1 = 1;
	public static final int KANJI_FILTER_N2 = 2;
	public static final int KANJI_FILTER_N3 = 3;
	public static final int KANJI_FILTER_N4 = 4;
	public static final int KANJI_FILTER_N5 = 5;
	public static final int KANJI_FILTER_FAVORITES = 6;
	
	//private static final String DB_PATH = "/data/data/shiba.test.androidkanji/databases/";
	private static final String DB_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +APP_NAME +File.separator;
	private static final String DB_NAME = "kanjidic2-en.db";
	private static final int DB_VERSION = 5;
	private static final int MAX_JLPT_LEVEL = 5;
	private final Context mCtx;
	private SQLiteDatabase mDb;
	private static final String FAVDB_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + APP_NAME + File.separator;
	private static final String FAVDB_NAME = "kanjifav.db";
	private SQLiteDatabase mFavDb;
	
	
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
	
	public void createDatabase() throws IOException{
		// NOTE: 	This method should be called ONLY into an AsyncTask because it may
		//			be time-consuming
		boolean kanjiDbExist = checkDatabase(DB_PATH + DB_NAME);
		
		if(!kanjiDbExist){
			// Note: what does the next line do? We don't use its return value.
			this.getReadableDatabase();
			try{
				copyDatabase();
			}catch(IOException e){
				throw new Error("Error copying database");
			}
			this.close();	
		}
	}
	
	public void updateJLPTLists(){
		
		SQLiteDatabase db = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READWRITE);
		for(int i=1; i<=MAX_JLPT_LEVEL; i++){
			try {
				
				InputStream inputStream = mCtx.getAssets().open("n" +i +"_list.txt");
				
				InputStreamReader inputreader = new InputStreamReader(inputStream);
	            BufferedReader buffreader = new BufferedReader(inputreader);
	            String kanji;
                while (( kanji = buffreader.readLine()) != null) {
                	updateJLPTLevelForEntry(db, i, TextTools.kanjiToCode(kanji));
                }
                       
				inputStream.close();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		db.close();
	}
	
	private void updateJLPTLevelForEntry(SQLiteDatabase db, int level, int entry){
		String query = "UPDATE " + TABLE_ENTRIES + " SET " + KEY_JLPT + "=" +level +" WHERE " +KEY_ID +"=" +entry;
		System.out.println(query);
		db.rawQuery(query, null);
	}
	
	private boolean checkDatabase(String dbName){
		
		SQLiteDatabase checkDB = null;
		try{
			// We try to open the DB
			checkDB = SQLiteDatabase.openDatabase(dbName, null, SQLiteDatabase.OPEN_READONLY);
		}catch(SQLiteException e){
			// Falling here means that the DB doesn't exist
			System.out.println("Database does not exist.");
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
		String dbPath = DB_PATH + DB_NAME;
		mDb = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY);
	}
	
	public synchronized void close(){
		if(mDb != null){
			mDb.close();
		}
		super.close();
	}
	
	public String fetchAllKanji(){
		String query = "SELECT " + KEY_ID +" FROM " + TABLE_ENTRIES + " WHERE " + KEY_JLPT + " IS NOT NULL";
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
				query = "SELECT " + KEY_ID + " FROM " + TABLE_ENTRIES + " WHERE " + KEY_JLPT + " IS " + group;
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
	
	public void createFavoriteDB(){
		File dbFile = new File(FAVDB_PATH + FAVDB_NAME);
		mFavDb = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
		mFavDb.close();
	}
}
