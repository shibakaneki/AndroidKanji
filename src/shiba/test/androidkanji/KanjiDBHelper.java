package shiba.test.androidkanji;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class KanjiDBHelper extends SQLiteOpenHelper {
	public static final String KEY_ID = "_id";
	public static final String KEY_GRADE = "grade";
	public static final String KEY_STROKE_COUNT = "strokeCount";
	public static final String KEY_FREQUENCY = "frequency";
	public static final String KEY_JLPT = "jlpt";
	public static final String KEY_PATH = "path";
	public static final String TABLE_ENTRIES = "entries";
	
	private static final String DB_PATH = "/data/data/shiba.test.androidkanji/databases/";
	private static final String DB_NAME = "kanjidic2-en.db";
	private static final int DB_VERSION = 5;
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
	
	public void createDatabase() throws IOException{
		boolean dbExist = checkDatabase();
		
		if(dbExist){
			// Nothing to do
		}else{
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
	
	private boolean checkDatabase(){
		String dbPath = DB_PATH + DB_NAME;
		SQLiteDatabase checkDB = null;
		try{
			// We try to open the DB
			checkDB = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY);
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
	
	public Cursor fetchAllKanji(){
		Cursor c = mDb.query(	TABLE_ENTRIES, 
				new String[]{KEY_ID}, 
				null, 
				null, 
				null, 
				null, 
				null);
		
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
}
