package ch.shibastudio.kanjinotepad;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class KanjiInfoView extends LinearLayout implements IKanjiListener{
	private Context mCtx;
	private TextView mOnView;
	private TextView mKunView;
	private TextView mJLPTView;
	private TextView mStrokesCountView;
	private TextView mMeaning;
	private TextView mBushu;
	
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
		mOnView = (TextView)findViewById(R.id.kanjiON);
		mKunView = (TextView)findViewById(R.id.kanjiKUN);
		mJLPTView = (TextView)findViewById(R.id.kanjiJLPT);
		mStrokesCountView = (TextView)findViewById(R.id.kanjiStrokeNbr);
		mMeaning = (TextView)findViewById(R.id.kanjiMeaning);
		mBushu = (TextView)findViewById(R.id.kanjiBushu);
		KanjiManager.addKanjiListener(this);
	}
	
	public void kanjiChanged(int codepoint){
		// JLPT
		int jlpt = KanjiManager.kanji().jlpt;
		if(0 < jlpt){
			mJLPTView.setText("" +KanjiManager.kanji().jlpt);
		}else{
			mJLPTView.setText("");
		}
		
		// Strokes number
		mStrokesCountView.setText("" +KanjiManager.kanji().strokeCount);
		
		// ON Yomi
		mOnView.setText("");
		String strON = "";
		for(int i=0; i<KanjiManager.kanji().oNYomi.size(); i++){
			if(0 < i){
				strON += ", ";
			}
			strON += KanjiManager.kanji().oNYomi.get(i);
		}
		mOnView.setText(strON);
		
		// KUN Yomi
		mKunView.setText("");
		String strKUN = "";
		for(int j=0; j<KanjiManager.kanji().kUNYomi.size(); j++){
			if(0 < j){
				strKUN += ", ";
			}
			strKUN += KanjiManager.kanji().kUNYomi.get(j);
		}
		mKunView.setText(strKUN);
		
		// Meaning
		mMeaning.setText("");
		String strMeaning = "";
		for(int k=0; k<KanjiManager.kanji().meaning.size(); k++){
			if(0 < k){
				strMeaning += ", ";
			}
			strMeaning += KanjiManager.kanji().meaning.get(k);
		}
		mMeaning.setText(strMeaning);
		
		// TODO: Handle the bushu infos here
	}
}
