package shiba.test.androidkanji;

import android.R.integer;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class KanjiAdapter extends BaseAdapter {

	private Cursor _kanjiCursor;
	private Context _context;
	private LayoutInflater _inflater;
	
	public KanjiAdapter(Context c, Cursor kanjis){
		_context = c;
		_kanjiCursor = kanjis;
		_inflater = LayoutInflater.from(_context);
		
		System.out.println("_kanjiCursor count: " + _kanjiCursor.getCount());
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
		LinearLayout layoutItem;
		
		if(null == convertView){
			layoutItem = (LinearLayout)_inflater.inflate(R.layout.kanji_row, parent, false);
		}else{
			layoutItem = (LinearLayout)convertView;
		}
		
		// Move the cursor to the right row (specified by position)
		_kanjiCursor.moveToPosition(position);
		
		// Get the value at the given position
		int index = _kanjiCursor.getColumnIndex(KanjiDBHelper.KEY_ID);
		TextView kanjiTV = (TextView)layoutItem.findViewById(R.id.text1);
		
		// TODO: convert the text value in UTF8 character
		String value = _kanjiCursor.getString(index);
		int iVal = Integer.parseInt(value);
		
		kanjiTV.setText(TextTools.codeToKanji(iVal));			
		
		return layoutItem;
	}
}
