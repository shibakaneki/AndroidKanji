package shiba.test.androidkanji;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class KanjiInfoView extends View implements IKanjiListener{
	
	public KanjiInfoView(Context c){
		super(c);
		init();
	}

	public KanjiInfoView(Context c, AttributeSet attrs){
		super(c, attrs);
		init();
	}
	
	public void kanjiChanged(int codepoint){
		
	}
	
	private void init(){
		KanjiManager.addKanjiListener(this);
	}
}
