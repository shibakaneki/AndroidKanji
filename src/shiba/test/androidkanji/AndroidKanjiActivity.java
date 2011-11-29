package shiba.test.androidkanji;

import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class AndroidKanjiActivity extends Activity {
	
	private KanjiDBHelper mKDBHelper;
	private KanjiDicHelper mKDicHelper;
	private ListView _kanjiListView;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        _kanjiListView = (ListView)findViewById(R.id.kanjiList);
        
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
    	// TODO: Find a way to in have a better xml parser in order to do that quickly.
    	ArrayList<String> kanjis = mKDicHelper.getAllKanji();
    	String str [] = (String []) kanjis.toArray (new String [kanjis.size ()]);
    	_kanjiListView.setAdapter(new ArrayAdapter<String>(this, R.layout.kanji_row, str));
    }
    
    private void fillData(){
    	
    	Cursor c = mKDBHelper.fetchAllKanji();
    	startManagingCursor(c);
    	
    	String[] from = new String[] {KanjiDBHelper.KEY_ID};
    	int[] to = new int[] {R.id.text1};
    	
    	SimpleCursorAdapter notes = new SimpleCursorAdapter(this, R.layout.kanji_row, c, from, to);
    	_kanjiListView.setAdapter(notes);
    	
    }
}
