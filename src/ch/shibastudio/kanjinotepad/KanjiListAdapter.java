package ch.shibastudio.kanjinotepad;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class KanjiListAdapter extends ArrayAdapter<KanjiInfo>{
	private ArrayList<KanjiInfo> mItems;
	private LayoutInflater mInflater;
	private KanjiDBHelper mDbHelper;
	private Context mCtx;
	
	private OnClickListener onCharacterClicked = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if(null != v){
				try {
					mDbHelper.openDatabase();
					Cursor c = mDbHelper.getKanjiInfos(TextTools.kanjiToCode(((TextView)v).getText().toString()));
					
					// Get the JLPT level
					int jlpt = c.getInt(c.getColumnIndex(KanjiDBHelper.KEY_JLPT));
					// Get the kanjiVG data
					String kvg = "";
					byte[] paths = c.getBlob(c.getColumnIndex(KanjiDBHelper.KEY_PATH));
					if(null != paths){
						kvg = ZipTools.decompress(paths);
					}
					// Get the stroke count
					int strokeCount = c.getInt(c.getColumnIndex(KanjiDBHelper.KEY_STROKE_COUNT));
					// Get the frequency
					int frequency = c.getInt(c.getColumnIndex(KanjiDBHelper.KEY_FREQUENCY));				
					// Get the grade
					int grade = c.getInt(c.getColumnIndex(KanjiDBHelper.KEY_GRADE));
					
					// TODO: Get the readings too!
					
					// Finally, update the current kanji
					KanjiInfo currentKanji = new KanjiInfo(((TextView)v).getText().toString(), false);
					currentKanji.frequency = frequency;
					currentKanji.grade = grade;
					currentKanji.kvg = kvg;
					currentKanji.strokeCount = strokeCount;
					currentKanji.jlpt = jlpt;
					KanjiManager.setCurrentKanji(currentKanji);
					
					mDbHelper.close();
				} catch (SQLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} 
    };
    
    private OnClickListener onFavoriteClicked = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if(null != v){
				int position = Integer.parseInt(v.getTag().toString());
				KanjiInfo ki = mItems.get(position);
				ki.toggleFavorite();
				Drawable d;
				if(ki.favorite){
					d = mCtx.getResources().getDrawable(R.drawable.favfull);
				}else{
					d = mCtx.getResources().getDrawable(R.drawable.favempty);
				}
				
				((ImageView)v).setImageDrawable(d);
				
				try {
					mDbHelper.openDatabase();
					mDbHelper.toggleFavorite(ki.codePoint);
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
		mItems = items;
		mInflater = LayoutInflater.from(context);
		mDbHelper = new KanjiDBHelper(context);
	}

	@Override
	public int getCount() {
		return mItems.size();
	}

	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout row;
		if(null == convertView){
			row = (LinearLayout)mInflater.inflate(R.layout.kanji_row, parent, false);
		}else{
			row = (LinearLayout)convertView;
		}
			
		KanjiInfo ki = mItems.get(position);
		
		TextView character = (TextView)row.findViewById(R.id.text1);
		ImageView star = (ImageView)row.findViewById(R.id.favoriteStar);
		character.setOnClickListener(onCharacterClicked);
		star.setOnClickListener(onFavoriteClicked);
		
		character.setText(ki.kanji);
		
		Drawable d;
		if(ki.favorite){
			d = mCtx.getResources().getDrawable(R.drawable.favfull);
		}else{
			d = mCtx.getResources().getDrawable(R.drawable.favempty);
		}
		star.setImageDrawable(d);
		star.setTag(position);
		
		return row;
    }
}
