package ch.shibastudio.kanjinotepad.drawing;

import ch.shibastudio.kanjinotepad.IKanjiListener;
import ch.shibastudio.kanjinotepad.KanjiManager;
import ch.shibastudio.kanjinotepad.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class DrawingPanel extends LinearLayout implements IKanjiListener{
	private Context mCtx;
	private ImageView mEraseBttn;
	private ImageView mCorrectBttn;
	private KanjiView mKanjiView;
	
	public DrawingPanel(Context c){
		super(c);
		mCtx = c;
		init();
	}
	
	public DrawingPanel(Context c, AttributeSet attrs){
		super(c, attrs);
		mCtx = c;
		init();
	}
	
	private void init(){
		LayoutInflater inflater = LayoutInflater.from(mCtx);
		inflater.inflate(R.layout.drawing_panel, this);
		KanjiManager.addKanjiListener(this);
		mEraseBttn = (ImageView)findViewById(R.id.eraseButton);
		mCorrectBttn = (ImageView)findViewById(R.id.correctButton);
		mKanjiView = (KanjiView)findViewById(R.id.drawingZone);
		
		mEraseBttn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				clear();
			}
		});
		
		mCorrectBttn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				correct();
			}
		});
	}
	
	public void kanjiChanged(int codepoint){
		clear();
	}
	
	private void clear(){
		if(null != mKanjiView){
			mKanjiView.clearCanvas();
		}
	}
	
	private void correct(){
		if(null != mKanjiView){
			mKanjiView.verifyDrawing();
		}
	}
}
