package shiba.test.androidkanji;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class KanjiListAdapter extends ArrayAdapter<KanjiInfo>{
	private ArrayList<KanjiInfo> items;
	private LayoutInflater mInflater;
	
	public KanjiListAdapter(Context context, int textViewResourceId, ArrayList<KanjiInfo> items) {
		super(context, textViewResourceId, items);
		this.items = items;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return items.size();
	}

	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {		
		KanjiRowView layoutItem;
		if(null == convertView){
			layoutItem = (KanjiRowView) mInflater.inflate(R.layout.kanji_charfav, parent, false);
		}else{
			layoutItem = (KanjiRowView)convertView;
		}
		
		layoutItem.setCodePoint(TextTools.kanjiToCode(items.get(position).kanji()));
		layoutItem.setFavorite(items.get(position).favorite());
		
		return layoutItem;
    }
}
