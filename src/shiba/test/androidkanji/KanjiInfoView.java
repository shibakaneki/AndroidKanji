package shiba.test.androidkanji;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

public class KanjiInfoView extends LinearLayout implements IKanjiListener{
	private Context mCtx;
	
	public KanjiInfoView(Context c){
		super(c);
		mCtx = c;
		init();
	}

	public KanjiInfoView(Context c, AttributeSet attrs){
		super(c, attrs);
		mCtx = c;
		init();
	}
	
	private void init(){
		LayoutInflater inflater = LayoutInflater.from(mCtx);
		inflater.inflate(R.layout.kanji_info, this);
		KanjiManager.addKanjiListener(this);
	}
	
	public void kanjiChanged(int codepoint){
		
	}
}
