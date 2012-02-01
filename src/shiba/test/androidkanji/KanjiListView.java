package shiba.test.androidkanji;

import java.sql.SQLException;

import android.content.Context;
import android.database.Cursor;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

public class KanjiListView extends LinearLayout {
	private KanjiDBHelper _KDBHelper;
	private Spinner _filterSpinner;
	private EditText _filterText;
	private ImageView _searchButton;
	private ListView _kanjiListView;
	private Context _ctx;
	
	public KanjiListView(Context c){
		super(c);
		_ctx = c;
		init();
	}
	
	public KanjiListView(Context c, AttributeSet attrs){
		super(c, attrs);
		_ctx = c;
		LayoutInflater inflater = LayoutInflater.from(c);
		inflater.inflate(R.layout.kanji_list, this);
		init();
	}
	
	private void init(){
		_kanjiListView = (ListView)findViewById(R.id.kanjiList);
        _kanjiListView.setEmptyView(findViewById(R.id.emptyKanjiView));
        _filterSpinner = (Spinner)findViewById(R.id.filterCategory);
        _KDBHelper = new KanjiDBHelper(_ctx);
        fillData();
        
        // TODO : rework that to make it clean
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
        	KanjiAdapter adapter = new KanjiAdapter(_ctx, c);
        	_kanjiListView.setAdapter(adapter);
        	_KDBHelper.close();
    	}catch(SQLException e){
    		throw new Error(e.getMessage());
    	}
    }

}
