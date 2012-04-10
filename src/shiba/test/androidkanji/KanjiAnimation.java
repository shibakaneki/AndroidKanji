package shiba.test.androidkanji;

import shiba.test.androidkanji.KanjiCanvas.KCMode;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

public class KanjiAnimation extends LinearLayout implements IKanjiListener{
	private Context _ctx;
	private KanjiCanvas _canvas;
	private KanjiVGParser _parser;
	
	public KanjiAnimation(Context c){
		super(c);
		_ctx = c;
		init();
	}
	
	public KanjiAnimation(Context c, AttributeSet attrs){
		super(c, attrs);
		_ctx = c;
		init();
	}
	
	private void init(){
		LayoutInflater inflater = LayoutInflater.from(_ctx);
		inflater.inflate(R.layout.kanji_anim, this);
		KanjiManager.addKanjiListener(this);
		_parser = new KanjiVGParser();
		//_kanjiListView = (KanjiListView)findViewById(R.id.kanjiListView1);
		_canvas = (KanjiCanvas)findViewById(R.id.kanjiAnimationCanvas);
		_canvas.setMode(KCMode.ANIMATION);
	}
	
	public void kanjiChanged(int codepoint){
		String kvg = KanjiManager.kanji().kvg();
		_parser.setCurrentKVG(kvg);
		_parser.parse();
		
		System.out.println("The current kanji has " +_parser.paths().size() +" path(s)");
		
		_canvas.setCurrentPaths(_parser.paths());
	}
}
