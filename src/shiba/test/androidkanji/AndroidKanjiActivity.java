package shiba.test.androidkanji;

import android.app.ListActivity;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.widget.SimpleCursorAdapter;


public class AndroidKanjiActivity extends ListActivity {
	
	private KanjiDBManager mDBMgr;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mDBMgr = new KanjiDBManager(this);
        try{
        	mDBMgr.open();
        } catch(SQLException e){
        	mDBMgr.createNewDatabase();
        	mDBMgr.open();
        }
        fillData();
    }
    
    private void fillData(){
    	Cursor c = mDBMgr.fetchAllKanji();
    	startManagingCursor(c);
    	
    	String[] from = new String[] {KanjiDBAdapter.KEY_ID};
    	int[] to = new int[] {R.id.text1};
    	
    	SimpleCursorAdapter notes = new SimpleCursorAdapter(this, R.layout.kanji_row, c, from, to);
    	setListAdapter(notes);
    }
}
