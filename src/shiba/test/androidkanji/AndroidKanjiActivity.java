package shiba.test.androidkanji;

import java.io.IOException;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class AndroidKanjiActivity extends Activity {
	
	private KanjiDBHelper _KDBHelper;
	private ListView _kanjiListView;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        _kanjiListView = (ListView)findViewById(R.id.kanjiList);
                
        // First, create the helpers
        _KDBHelper = new KanjiDBHelper(this);
        
        try{
        	// If the database is not created, create it
        	_KDBHelper.createDatabase();
        }catch(IOException e){
        	throw new Error("Unable to create database!");
        }
        
        try{
        	_KDBHelper.openDatabase();
        }catch(java.sql.SQLException e){
        	throw new Error(e.getMessage());
        }
        
        //populateKanjiList();
        fillData();
        _KDBHelper.close();
    }
    
    private void fillData(){
    	Cursor c = _KDBHelper.fetchAllKanji();
    	KanjiAdapter adapter = new KanjiAdapter(this, c);
    	_kanjiListView.setAdapter(adapter);
    }
}
