package shiba.test.androidkanji;

import java.sql.SQLException;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class KanjiDBAdapter{
	
	public static final String KEY_ID = "id";
	public static final String KEY_GRADE = "grade";
	public static final String KEY_STROKE_COUNT = "strokeCount";
	public static final String KEY_FREQUENCY = "frequency";
	public static final String KEY_JLPT = "jlpt";
	public static final String KEY_PATH = "path";
	
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	
	private static final String DATABASE_NAME = "kanjidic2-en";
	private static final int DATABASE_VERSION = 5;
	private static final String TABLE_ENTRIES = "entries";
	private final Context mCtx;
	
	private static class DatabaseHelper extends SQLiteOpenHelper{
		DatabaseHelper(Context context){
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			
		}
	}
	
	public KanjiDBAdapter(Context ctx){
		mCtx = ctx;
	}

	public KanjiDBAdapter open() throws SQLException{
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getReadableDatabase();
		return this;
	}
	
	public void close(){
		mDbHelper.close();
	}
	
	public Cursor fetchAllKanji(){
		return mDb.query(	TABLE_ENTRIES, 
							new String[]{KEY_ID, KEY_GRADE, KEY_STROKE_COUNT, KEY_FREQUENCY, KEY_JLPT, KEY_PATH}, 
							null, 
							null, 
							null, 
							null, 
							null);
	}
	
	public Cursor fetchKanji(long rowId){
		Cursor mCursor =

            mDb.query(true, TABLE_ENTRIES, new String[] {KEY_ID,
                    KEY_GRADE, KEY_STROKE_COUNT, KEY_FREQUENCY, KEY_JLPT, KEY_PATH}, KEY_ID + "=" + rowId, null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
	}
}
