package shiba.test.androidkanji;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class DrawingView extends View{

	private Context mCtx;
	
	public DrawingView(Context c){
		super(c);
		mCtx = c;
	}
	
	public DrawingView(Context c, AttributeSet attrs){
		super(c, attrs);
		mCtx = c;
	}
	
}
