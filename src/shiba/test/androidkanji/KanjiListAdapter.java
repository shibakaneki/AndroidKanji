package shiba.test.androidkanji;

import java.sql.SQLException;
import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;

public class KanjiListAdapter extends ArrayAdapter<KanjiInfo>{
	private ArrayList<KanjiInfo> items;
	private LayoutInflater mInflater;
	private KanjiDBHelper mDbHelper;
	
	private OnClickListener onCharacterClicked = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO: getParent().getParent() is the proof of a bad design, rework that one day..
			KanjiRowView kanjiRow = (KanjiRowView)v.getParent().getParent();
			// TODO : Get the kanji infos and update the other fragments
		} 
    };
    
    private OnClickListener onFavoriteClicked = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO: getParent().getParent() is the proof of a bad design, rework that one day..
			KanjiRowView kanjiRow = (KanjiRowView)v.getParent().getParent();
			kanjiRow.setFavorite(!kanjiRow.favorite());
			items.get(kanjiRow.index()).toggleFavorite();
			
			try {
				mDbHelper.openDatabase();
				mDbHelper.toggleFavorite(kanjiRow.codePoint());
				mDbHelper.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} 
    };
	
	public KanjiListAdapter(Context context, int textViewResourceId, ArrayList<KanjiInfo> items) {
		super(context, textViewResourceId, items);
		this.items = items;
		mInflater = LayoutInflater.from(context);
		mDbHelper = new KanjiDBHelper(context);
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
		layoutItem.setIndex(position);
		layoutItem.character().setOnClickListener(onCharacterClicked);
		layoutItem.favIcon().setOnClickListener(onFavoriteClicked);
		
		return layoutItem;
    }
}
