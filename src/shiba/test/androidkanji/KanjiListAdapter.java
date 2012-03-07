package shiba.test.androidkanji;

import java.sql.SQLException;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class KanjiListAdapter extends ArrayAdapter<KanjiInfo>{
	private ArrayList<KanjiInfo> items;
	private LayoutInflater mInflater;
	private KanjiDBHelper mDbHelper;
	private Context mCtx;
	
	private OnClickListener onCharacterClicked = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if(null != v){
				// TODO : Get the kanji infos and update the other fragments
				Toast.makeText(getContext(), ((TextView)v).getText(), Toast.LENGTH_SHORT).show();
			}
		} 
    };
    
    private OnClickListener onFavoriteClicked = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if(null != v){
				int position = Integer.parseInt(v.getTag().toString());
				KanjiInfo ki = items.get(position);
				ki.toggleFavorite();
				Drawable d;
				if(ki.favorite()){
					d = mCtx.getResources().getDrawable(R.drawable.favfull);
				}else{
					d = mCtx.getResources().getDrawable(R.drawable.favempty);
				}
				
				((ImageView)v).setImageDrawable(d);
				
				try {
					mDbHelper.openDatabase();
					mDbHelper.toggleFavorite(TextTools.kanjiToCode(ki.kanji()));
					mDbHelper.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} 
    };
	
	public KanjiListAdapter(Context context, int textViewResourceId, ArrayList<KanjiInfo> items) {
		super(context, textViewResourceId, items);
		mCtx = context;
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
		LinearLayout row;
		if(null == convertView){
			row = (LinearLayout)mInflater.inflate(R.layout.kanji_row, parent, false);
		}else{
			row = (LinearLayout)convertView;
		}
			
		KanjiInfo ki = items.get(position);
		
		TextView character = (TextView)row.findViewById(R.id.text1);
		ImageView star = (ImageView)row.findViewById(R.id.favoriteStar);
		character.setOnClickListener(onCharacterClicked);
		star.setOnClickListener(onFavoriteClicked);
		
		character.setText(ki.kanji());
		
		Drawable d;
		if(ki.favorite()){
			d = mCtx.getResources().getDrawable(R.drawable.favfull);
		}else{
			d = mCtx.getResources().getDrawable(R.drawable.favempty);
		}
		star.setImageDrawable(d);
		star.setTag(position);
		
		return row;
    }
}
