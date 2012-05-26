package ch.shibastudio.kanjinotepad;

import shiba.test.androidkanji.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

public class KanjiPlayerContainer extends LinearLayout implements IKanjiListener{
	private Context mCtx;
	private KanjiVGParser mParser;
	private KanjiPlayer mPlayer;
	
	public KanjiPlayerContainer(Context c){
		super(c);
		mCtx = c;
		init();
	}
	
	public KanjiPlayerContainer(Context c, AttributeSet attrs){
		super(c, attrs);
		mCtx = c;
		init();
	}
	
	private void init(){
		LayoutInflater inflater = LayoutInflater.from(mCtx);
		inflater.inflate(R.layout.kanji_player, this);
		KanjiManager.addKanjiListener(this);
		mParser = new KanjiVGParser();;		
		mPlayer = (KanjiPlayer)findViewById(R.id.kanjiPlayer);
	}
	
	public void kanjiChanged(int codepoint){
		mPlayer.flushKanjiPath();
		mParser.kvg = KanjiManager.kanji().kvg;
		mParser.parse();
		mPlayer.setCurrentPaths(mParser.kanjiInfo);
	}
}
