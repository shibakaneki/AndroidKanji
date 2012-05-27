package ch.shibastudio.kanjinotepad;

import ch.shibastudio.kanjinotepad.KanjiCanvas.KCMode;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

public class KanjiAnimation extends LinearLayout implements IKanjiListener{
	private Context mCtx;
	private KanjiCanvas mCanvas;
	private KanjiVGParser mParser;
	
	public KanjiAnimation(Context c){
		super(c);
		mCtx = c;
		init();
	}
	
	public KanjiAnimation(Context c, AttributeSet attrs){
		super(c, attrs);
		mCtx = c;
		init();
	}
	
	private void init(){
		LayoutInflater inflater = LayoutInflater.from(mCtx);
		inflater.inflate(R.layout.kanji_anim, this);
		KanjiManager.addKanjiListener(this);
		mParser = new KanjiVGParser();
		mCanvas = (KanjiCanvas)findViewById(R.id.kanjiAnimationCanvas);
		mCanvas.setMode(KCMode.ANIMATION);
	}
	
	public void kanjiChanged(int codepoint){
		mCanvas.flushKanjiPath();
		mParser.kvg = KanjiManager.kanji().kvg;
		mParser.parse();
		mCanvas.setCurrentPaths(mParser.kanjiInfo);
		
		// By default, the animation is started automatically when the kanji changes
		mCanvas.startAnimation();
	}
}
