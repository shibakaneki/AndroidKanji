package shiba.test.androidkanji;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class KanjiAdapter extends BaseAdapter{

	private Cursor _kanjiCursor;
	private Context _context;
	private LayoutInflater _inflater;
	private KanjiDBHelper _dbHelper;
	
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
				
		return layoutItem;
	}
}
