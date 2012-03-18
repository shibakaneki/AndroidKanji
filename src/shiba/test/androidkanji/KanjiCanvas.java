package shiba.test.androidkanji;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class KanjiCanvas extends View{

	public enum KCMode{
		DRAWING, ANIMATION
	}
	
	private Context mCtx;
	private Paint mPainter;
	private int mPenColor;
	private float mPenWidth;
	private boolean mDrawing = false;
	private float mOldX;
	private float mOldY;
	private float mCrntX;
	private float mCrntY;
	private ArrayList<Line> mLines = new ArrayList<Line>();
	private int mGuideBorder = 10;
	private KCMode mMode = KCMode.DRAWING;
	
	public KanjiCanvas(Context c){
		super(c);
		mCtx = c;
		init();
	}
	
	public KanjiCanvas(Context c, AttributeSet attrs){
		super(c, attrs);
		mCtx = c;
		init();
	}
	
	private void init(){
		mPenColor = Color.BLUE;
		mPenWidth = 7f;
		mPainter = new Paint();
		mPainter.setDither(true);
		mPainter.setColor(mPenColor);
		mPainter.setStyle(Paint.Style.STROKE);
		mPainter.setStrokeJoin(Paint.Join.ROUND);
		mPainter.setStrokeCap(Paint.Cap.ROUND);
		mPainter.setStrokeWidth(mPenWidth);
	}
	
	public void setMode(KCMode mode){
		mMode = mode;
	}
	
	@Override
	protected void onDraw(Canvas c){
		System.out.println("(" +getWidth() +";" +getHeight() +")");
		// Draw the persistent parts
		mPainter.setColor(Color.BLACK);
		mPainter.setAntiAlias(false);
		
		// Guides
		mPainter.setStrokeWidth(3f);
		mPainter.setPathEffect(null);
		int guideWidth = guideWidth();
		int guideHeight = guideWidth;
		Rect guide = new Rect(	mGuideBorder, mGuideBorder, mGuideBorder + guideWidth, mGuideBorder + guideHeight);
		c.drawRect(guide, mPainter);
		c.drawLine(mGuideBorder +guideWidth/2, mGuideBorder, mGuideBorder +guideWidth/2, mGuideBorder + guideHeight, mPainter);
		c.drawLine(mGuideBorder, mGuideBorder + guideHeight/2, mGuideBorder + guideWidth, mGuideBorder + guideHeight/2, mPainter);
		mPainter.setStrokeWidth(1f);
		mPainter.setPathEffect(new DashPathEffect(new float[]{3, 3}, 0));
		c.drawLine(mGuideBorder, mGuideBorder + guideHeight/4, mGuideBorder + guideWidth, mGuideBorder + guideHeight/4, mPainter);
		c.drawLine(mGuideBorder, mGuideBorder + 3*guideHeight/4, mGuideBorder + guideWidth, mGuideBorder + 3*guideHeight/4, mPainter);
		c.drawLine(mGuideBorder + guideWidth/4, mGuideBorder, mGuideBorder + guideWidth/4, mGuideBorder + guideHeight, mPainter);
		c.drawLine(mGuideBorder + 3*guideWidth/4, mGuideBorder, mGuideBorder + 3*guideWidth/4, mGuideBorder + guideHeight, mPainter);
		
		mPainter.setColor(mPenColor);
		mPainter.setPathEffect(null);
		mPainter.setStrokeWidth(mPenWidth);
		mPainter.setAntiAlias(true);
		
		// If needed, add new user lines
		if(mDrawing){
			mLines.add(new Line(mOldX, mOldY, mCrntX, mCrntY));
		}
		
		// Draw the lines done by the user
		for(int i=0; i<mLines.size(); i++){
			Line l = mLines.get(i);
			c.drawLine(l.xOrigin, l.yOrigin, l.xDest, l.yDest, mPainter);
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:
			if(KCMode.DRAWING == mMode && isInGuide(x, y)){
				mDrawing = true;
				mCrntX = event.getX();
				mCrntY = event.getY();
				mOldX = mCrntX;
				mOldY = mCrntY;
			}
			break;
			
		case MotionEvent.ACTION_MOVE:
			if(KCMode.DRAWING == mMode && isInGuide(x, y)){
				mDrawing = true;
				mOldX = mCrntX;
				mOldY = mCrntY;
				mCrntX = event.getX();
				mCrntY = event.getY();
			}
			break;
			
		case MotionEvent.ACTION_UP:
			if(KCMode.DRAWING == mMode && isInGuide(x, y)){
				mDrawing = false;
			}
			break;
			
		default:
			if(KCMode.DRAWING == mMode && isInGuide(x, y)){
				mDrawing = false;
			}
			break;	
		}
		// Refresh the view
		invalidate();
		return true;
	}
	
	private boolean isInGuide(float x, float y){
		int guideHeight = guideWidth();
		if(x >= mGuideBorder && x <= mGuideBorder + guideWidth() && y >= mGuideBorder && y <= mGuideBorder + guideHeight){
			return true;
		}
		return false;
	}
	
	private int guideWidth(){
		int w = getWidth();
		int h = getHeight();
		
		if(w < h){
			return w - 2*mGuideBorder;
		}else{
			return h - 2*mGuideBorder;
		}
	}
}
