package shiba.test.androidkanji;

import java.sql.SQLException;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;
import android.widget.Spinner;

public class AndroidKanjiActivity extends Activity {
	
	private KanjiDBHelper _KDBHelper;
	private ListView _kanjiListView;
	private Spinner _filterSpinner;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        _kanjiListView = (ListView)findViewById(R.id.kanjiList);
        _kanjiListView.setEmptyView(findViewById(R.id.emptyKanjiView));
        _filterSpinner = (Spinner)findViewById(R.id.filterCategory);
        _KDBHelper = new KanjiDBHelper(this);
        
        // Add default datas
        fillData();
        
        // Connect to the events
        _filterSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id){
				int category = KanjiDBHelper.KANJI_FILTER_ALL;
				if(0 < pos){
					if(1 == pos){
						// Favorites
						category = KanjiDBHelper.KANJI_FILTER_FAVORITES;
					}else{
						category = pos - 1;
					}
				}else{
					// All kanji
					category = KanjiDBHelper.KANJI_FILTER_ALL;
				}
				getKanjis(category);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
    }
    
    private void fillData(){
    	getKanjis(KanjiDBHelper.KANJI_FILTER_ALL);
    }
    
    public void getKanjis(int category){
    	try{
    		_KDBHelper.openDatabase();
        	Cursor c = _KDBHelper.fetchKanji(category);
        	KanjiAdapter adapter = new KanjiAdapter(this, c);
        	_kanjiListView.setAdapter(adapter);
        	_KDBHelper.close();
    	}catch(SQLException e){
    		throw new Error(e.getMessage());
    	}
    }
}
