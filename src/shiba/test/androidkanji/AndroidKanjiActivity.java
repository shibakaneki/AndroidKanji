package shiba.test.androidkanji;

import java.sql.SQLException;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;

public class AndroidKanjiActivity extends Activity {
	
	private KanjiDBHelper _KDBHelper;
	private ListView _kanjiListView;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        _kanjiListView = (ListView)findViewById(R.id.kanjiList);
        _kanjiListView.setEmptyView(findViewById(R.id.emptyKanjiView));
        _KDBHelper = new KanjiDBHelper(this);
        
        fillData();       
    }
    
    private void fillData(){
    	try{
    		_KDBHelper.openDatabase();
        	Cursor c = _KDBHelper.fetchAllKanji();
        	KanjiAdapter adapter = new KanjiAdapter(this, c);
        	_kanjiListView.setAdapter(adapter);
        	_KDBHelper.close();
    	}catch(SQLException e){
    		throw new Error(e.getMessage());
    	}
    }
}
