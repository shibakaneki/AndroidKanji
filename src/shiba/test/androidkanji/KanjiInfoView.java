package shiba.test.androidkanji;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class KanjiInfoView extends View{
	private Context mCtx;
	
	public KanjiInfoView(Context c){
		super(c);
		mCtx = c;
	}

	public KanjiInfoView(Context c, AttributeSet attrs){
		super(c, attrs);
		mCtx = c;
	}
}
