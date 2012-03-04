package shiba.test.androidkanji;

import java.sql.SQLException;
import java.util.ArrayList;

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
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class KanjiListView extends LinearLayout {
	// -- Members ------------------------------------------------------------------
	private KanjiDBHelper mKDBHelper;
	private Spinner mFilterSpinner;
	private EditText mFilterText;
	private ImageView mSearchButton;
	private ListView mKanjiListView;
	private Context mCtx;
	
	// -- Action Listeners ---------------------------------------------------------
	private OnItemSelectedListener mFilterSelected = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
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
	};
	
	public KanjiListView(Context c){
		super(c);
		mCtx = c;
		init();
	}
	
	public KanjiListView(Context c, AttributeSet attrs){
		super(c, attrs);
		mCtx = c;
		LayoutInflater inflater = LayoutInflater.from(c);
		inflater.inflate(R.layout.kanji_list, this);
		init();
	}
	
	private void init(){
		mKanjiListView = (ListView)findViewById(R.id.kanjiList);
        mKanjiListView.setEmptyView(findViewById(R.id.emptyKanjiView));
        mFilterSpinner = (Spinner)findViewById(R.id.filterCategory);
        mKDBHelper = new KanjiDBHelper(mCtx);
        fillData();

        mFilterSpinner.setOnItemSelectedListener(mFilterSelected);
	}
	
	private void fillData(){
    	getKanjis(KanjiDBHelper.KANJI_FILTER_ALL);
    }
    
    public void getKanjis(int category){
    	try{
    		mKDBHelper.openDatabase();
        	Cursor c = mKDBHelper.fetchKanji(category);
        	mKDBHelper.close();
        	if(0 != c.getCount()){
        		ArrayList<KanjiInfo> results = new ArrayList<KanjiInfo>();
        		c.moveToFirst();
        		for(int i=0; i<c.getCount(); i++){
        			// Get the value at the given position
        			int index = c.getColumnIndex(KanjiDBHelper.KEY_ID);
        			String value = c.getString(index);
        			int iVal = Integer.parseInt(value);        					
        			// Now we will add the favorite star
        			int favIndex = c.getColumnIndex(KanjiDBHelper.KEY_STATE);		
        			int iFavVal = c.getInt(favIndex);
        			
        			KanjiInfo ki = new KanjiInfo(TextTools.codeToKanji(iVal), (0 < iFavVal));
        			results.add(ki);	
        			c.moveToNext();
        		}
        		
        		
        		KanjiListAdapter adapter = new KanjiListAdapter(mCtx, R.layout.kanji_charfav, results);
        		mKanjiListView.setAdapter(adapter);
        	}
 
    	}catch(SQLException e){
    		throw new Error(e.getMessage());
    	}
    }

}
