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
	private final int GUIDE_BORDER = 10;
	private int mWidthBorder;
	private int mHeightBorder;
	private KCMode mMode = KCMode.DRAWING;
	private boolean mShowGrid = true;
	
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
	
	public void setGridVisible(boolean visible){
		mShowGrid = visible;
	}
	
	@Override
	protected void onDraw(Canvas c){
		// Draw the persistent parts
		mPainter.setColor(Color.BLACK);
		mPainter.setAntiAlias(false);
		
		// Guides
		mPainter.setStrokeWidth(3f);
		mPainter.setPathEffect(null);
		int guideWidth = guideWidth();
		int guideHeight = guideWidth;
		Rect guide = new Rect(mWidthBorder, mHeightBorder, mWidthBorder + guideWidth, mHeightBorder + guideHeight);
		c.drawRect(guide, mPainter);
		
		if(mShowGrid){
			c.drawLine(mWidthBorder +guideWidth/2, mHeightBorder, mWidthBorder +guideWidth/2, mHeightBorder + guideHeight, mPainter);
			c.drawLine(mWidthBorder, mHeightBorder + guideHeight/2, mWidthBorder + guideWidth, mHeightBorder + guideHeight/2, mPainter);
			mPainter.setStrokeWidth(1f);
			mPainter.setPathEffect(new DashPathEffect(new float[]{3, 3}, 0));
			c.drawLine(mWidthBorder, mHeightBorder + guideHeight/4, mWidthBorder + guideWidth, mHeightBorder + guideHeight/4, mPainter);
			c.drawLine(mWidthBorder, mHeightBorder + 3*guideHeight/4, mWidthBorder + guideWidth, mHeightBorder + 3*guideHeight/4, mPainter);
			c.drawLine(mWidthBorder + guideWidth/4, mHeightBorder, mWidthBorder + guideWidth/4, mHeightBorder + guideHeight, mPainter);
			c.drawLine(mWidthBorder + 3*guideWidth/4, mHeightBorder, mWidthBorder + 3*guideWidth/4, mHeightBorder + guideHeight, mPainter);
		}	
		
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
		if(x >= mWidthBorder && x <= mWidthBorder + guideWidth() && y >= mHeightBorder && y <= mHeightBorder + guideHeight){
			return true;
		}
		return false;
	}
	
	private int guideWidth(){
		int w = getWidth();
		int h = getHeight();
		
		if(w < h){
			mWidthBorder = GUIDE_BORDER;
			mHeightBorder = (h-w)/2;
			return w - 2*mWidthBorder;
		}else{
			mHeightBorder = GUIDE_BORDER;
			mWidthBorder = (w-h)/2;
			return h - 2*mHeightBorder;
		}
	}
}
