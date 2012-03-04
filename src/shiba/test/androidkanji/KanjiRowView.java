package shiba.test.androidkanji;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class KanjiRowView extends LinearLayout {
	private int mCodePoint;
	private boolean mFavorite;
	private TextView mCharacter;
	private ImageView mFavIcon;
	private Context mCtx;
	private int mIndex;
	
	public KanjiRowView(Context c){
		super(c);
		mCtx = c;
		init();
	}
	
	public KanjiRowView(Context c, AttributeSet attrs){
		super(c, attrs);
		mCtx = c;
		LayoutInflater inflater = LayoutInflater.from(c);
		inflater.inflate(R.layout.kanji_row, this);
		init();
	}
	
	private void init(){
		mCodePoint = 0;
		mFavorite = false;
		mCharacter = (TextView)findViewById(R.id.text1);
		mFavIcon = (ImageView)findViewById(R.id.favoriteStar);
	}
	
	public int codePoint(){
		return mCodePoint;
	}
	
	public boolean favorite(){
		return mFavorite;
	}
	
	public void setCodePoint(int code){
		mCodePoint = code;
		String character = TextTools.codeToKanji(code);
		mCharacter.setText(character);
	}
	
	public void setFavorite(boolean fav){
		mFavorite = fav;
		Drawable d;
		if(fav){
			d = mCtx.getResources().getDrawable(R.drawable.favfull);
		}else{
			d = mCtx.getResources().getDrawable(R.drawable.favempty);
		}
		
		mFavIcon.setImageDrawable(d);
	}
	
	public TextView character(){
		return mCharacter;
	}
	
	public ImageView favIcon(){
		return mFavIcon;
	}
	
	public void setIndex(int i){
		mIndex = i;
	}
	
	public int index(){
		return mIndex;
	}
}
