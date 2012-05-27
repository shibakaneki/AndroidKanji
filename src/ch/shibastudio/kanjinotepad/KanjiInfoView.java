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
		KanjiManager.addKanjiListener(this);
	}
	
	public void kanjiChanged(int codepoint){
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
	}
}
