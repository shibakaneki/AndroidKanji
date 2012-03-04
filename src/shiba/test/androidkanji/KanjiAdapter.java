package shiba.test.androidkanji;

import java.sql.SQLException;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class KanjiAdapter extends BaseAdapter{

	private Cursor _kanjiCursor;
	private Context _context;
	private LayoutInflater _inflater;
	private KanjiDBHelper _dbHelper;
	
	private OnClickListener onCharacterClicked = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO: getParent().getParent() is the proof of a bad design, rework that one day..
			KanjiRowView kanjiRow = (KanjiRowView)v.getParent().getParent();
			// TODO: update the other views with the infos related to the clicked character
		} 
    };
    
    private OnClickListener onFavoriteClicked = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO: getParent().getParent() is the proof of a bad design, rework that one day..
			KanjiRowView kanjiRow = (KanjiRowView)v.getParent().getParent();
			kanjiRow.setFavorite(!kanjiRow.favorite());
			
			try {
				_dbHelper.openDatabase();
				_dbHelper.toggleFavorite(kanjiRow.codePoint());
				_kanjiCursor = _dbHelper.refresh();
				_dbHelper.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
    };
	
	public KanjiAdapter(Context c, Cursor kanjis){
		_context = c;
		_kanjiCursor = kanjis;
		_inflater = LayoutInflater.from(_context);
		_dbHelper = new KanjiDBHelper(_context);
	}

	@Override
	public int getCount() {
		return _kanjiCursor.getCount();
	}

	@Override
	public Object getItem(int position) {
		return _kanjiCursor.getString(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		KanjiRowView layoutItem;

		if(null == convertView){
			layoutItem = (KanjiRowView) _inflater.inflate(R.layout.kanji_charfav, parent, false);
		}else{
			layoutItem = (KanjiRowView)convertView;
		}
	
		// Move the cursor to the right row (specified by position)
		_kanjiCursor.moveToPosition(position);
		
		// Get the value at the given position
		int index = _kanjiCursor.getColumnIndex(KanjiDBHelper.KEY_ID);
		String value = _kanjiCursor.getString(index);
		int iVal = Integer.parseInt(value);
		layoutItem.setCodePoint(iVal);
				
		// Now we will add the favorite star
		int favIndex = _kanjiCursor.getColumnIndex(KanjiDBHelper.KEY_STATE);		
		int iFavVal = _kanjiCursor.getInt(favIndex);

		layoutItem.setFavorite((iFavVal > 0)?true:false);
		layoutItem.character().setOnClickListener(onCharacterClicked);
		layoutItem.favIcon().setOnClickListener(onFavoriteClicked);
				
		return layoutItem;
	}
}
