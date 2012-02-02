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
	
	private OnItemClickListener mKanjiListClicked = new OnItemClickListener() {
        public void onItemClick(AdapterView parent, View v, int position, long id)
        {
        	KanjiRowView kanji = (KanjiRowView)v;
            Toast.makeText(mCtx,"CodePoint: " +kanji.codePoint() +" favorite: " +kanji.favorite(),Toast.LENGTH_SHORT).show();
            
            // TODO : Now that I can retrieve the kanji datas, I must be able to determine if the user clicked on the character or on the favorite star.
            
        }
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
        mKanjiListView.setOnItemClickListener(mKanjiListClicked);
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
        	KanjiAdapter adapter = new KanjiAdapter(mCtx, c);
        	mKanjiListView.setAdapter(adapter);
        	mKDBHelper.close();
    	}catch(SQLException e){
    		throw new Error(e.getMessage());
    	}
    }

}
