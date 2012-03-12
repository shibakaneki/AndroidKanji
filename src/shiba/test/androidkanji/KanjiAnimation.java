package shiba.test.androidkanji;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class KanjiAnimation extends View{
	private Context mCtx;
	
	public KanjiAnimation(Context c){
		super(c);
		mCtx = c;
	}
	
	public KanjiAnimation(Context c, AttributeSet attrs){
		super(c, attrs);
		mCtx = c;
	}
}
