package shiba.test.androidkanji;

import java.io.IOException;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.SimpleCursorAdapter;

public class AndroidKanjiActivity extends ListActivity {
	
	private KanjiDBHelper mKDBHelper;
	private KanjiDicHelper mKDicHelper;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // First, create the helpers
        mKDBHelper = new KanjiDBHelper(this);
        mKDicHelper = new KanjiDicHelper(this);
        
        try{
        	// If the database is not created, create it
        	mKDBHelper.createDatabase();
        }catch(IOException e){
        	throw new Error("Unable to create database!");
        }
        
        try{
        	mKDBHelper.openDatabase();
        }catch(java.sql.SQLException e){
        	throw new Error(e.getMessage());
        }
        
        populateKanjiList();
        //fillData();
        mKDBHelper.close();
    }
    
    private void populateKanjiList()
    {
    	mKDicHelper.getAllKanji();
    	
    	// TODO: populate the kanji ListView
    	
    	//setListAdapter(new ArrayAdapter<String>(this, R.layout.kanji_row, mKDicHelper.getAllKanji()));
    }
    
    private void fillData(){
    	
    	Cursor c = mKDBHelper.fetchAllKanji();
    	startManagingCursor(c);
    	
    	String[] from = new String[] {KanjiDBHelper.KEY_ID};
    	int[] to = new int[] {R.id.text1};
    	
    	SimpleCursorAdapter notes = new SimpleCursorAdapter(this, R.layout.kanji_row, c, from, to);
    	setListAdapter(notes);
    	
    }
}
