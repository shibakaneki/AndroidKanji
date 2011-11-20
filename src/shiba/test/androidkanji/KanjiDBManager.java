package shiba.test.androidkanji;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import shiba.test.androidkanji.KanjiDBAdapter.DatabaseHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class KanjiDBManager extends SQLiteOpenHelper{
	
	private final Context mContext;
	private final String DB_PATH = "/data/data/shiba.test.androidkanji/databases/";
	private static final String TAG = "DBManager";
	private static final String DB_NAME = "kanjidic2-en.db";
	private static final Integer DB_VERSION = 1;
	private SQLiteDatabase mDB;
	private KanjiDBManager mDBManager;
	
	private static final String KEY_ID = "_id";
	private static final String KEY_GRADE = "grade";
	private static final String KEY_STROKE_COUNT = "strokeCount";
	private static final String KEY_FREQUENCY = "frequency";
	private static final String KEY_JLPT = "jlpt";
	private static final String KEY_PATH = "path";
	private static final String TABLE_ENTRIES = "entries";
	
	public KanjiDBManager(Context c){
		super(c, DB_NAME, null, DB_VERSION);
		mContext = c;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// Not implemented
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
	public KanjiDBManager open(){
		mDBManager = new KanjiDBManager(mContext);
		mDB = mDBManager.getReadableDatabase();
		return this;
	}
	
	public void createNewDatabase(){
		InputStream assetDB = null;
		try{
			assetDB = mContext.getAssets().open(DB_NAME);
			OutputStream dbOut = new FileOutputStream(DB_PATH + DB_NAME);
			byte[] buffer = new byte[1024];
			int length;
			while((length = assetDB.read(buffer)) > 0){
				dbOut.write(buffer, 0, length);
			}
			dbOut.flush();
			dbOut.close();
			assetDB.close();
			Log.i(TAG, "New database created...");
		}
		catch(IOException e){
			Log.e(TAG, "Could not create new database...");
            e.printStackTrace();
		}
	}
	
	public Cursor fetchAllKanji(){
		return mDB.query(	TABLE_ENTRIES, 
							new String[]{KEY_ID, KEY_GRADE, KEY_STROKE_COUNT, KEY_FREQUENCY, KEY_JLPT, KEY_PATH}, 
							null, 
							null, 
							null, 
							null, 
							null);
	}
	
	public Cursor fetchKanji(long rowId){
		Cursor mCursor =

            mDB.query(true, TABLE_ENTRIES, new String[] {KEY_ID,
                    KEY_GRADE, KEY_STROKE_COUNT, KEY_FREQUENCY, KEY_JLPT, KEY_PATH}, KEY_ID + "=" + rowId, null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
	}
}
