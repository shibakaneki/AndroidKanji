package shiba.test.androidkanji;

import java.sql.SQLException;
import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;


public class AndroidKanjiActivity extends ListActivity {
	
	private KanjiDBAdapter mDBHelper;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mDBHelper = new KanjiDBAdapter(this);
        try {
			mDBHelper.open();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println( e.getMessage() );
		}
        fillData();
    }
    
    private void fillData(){
    	Cursor c = mDBHelper.fetchAllKanji();
    	startManagingCursor(c);
    	
    	String[] from = new String[] {KanjiDBAdapter.KEY_ID};
    	int[] to = new int[] {R.id.text1};
    	
    	SimpleCursorAdapter notes = new SimpleCursorAdapter(this, R.layout.kanji_row, c, from, to);
    	setListAdapter(notes);
    }
}
