package ch.shibastudio.kanjinotepad.drawing;

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

public class DrawingView extends View{
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
	private int mGuideNumber = 4;
	
	public DrawingView(Context c){
		super(c);
		initPainter();
	}
	
	public DrawingView(Context c, AttributeSet attrs){
		super(c, attrs);
		initPainter();
	}
	
	private void initPainter(){
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

	@Override
	protected void onDraw(Canvas c){
		// Draw the persistent parts
		mPainter.setColor(Color.BLACK);
		mPainter.setAntiAlias(false);
		
		// Guides
		int iGuideStep = (int)(getWidth() / mGuideNumber);
		for(int i=0; i<mGuideNumber; i++){
			mPainter.setStrokeWidth(3f);
			mPainter.setPathEffect(null);
			int guideWidth = iGuideStep - 2*mGuideBorder;
			int guideHeight = guideWidth;
			Rect guide = new Rect(	i*iGuideStep + mGuideBorder, 
									(getHeight() - guideHeight) / 2, 
									((i+1)*iGuideStep) - mGuideBorder, 
									getHeight() - (getHeight() - guideHeight) / 2);
			c.drawRect(guide, mPainter);
			c.drawLine(i*iGuideStep + mGuideBorder +guideWidth/2, (getHeight() - guideHeight) / 2, i*iGuideStep + mGuideBorder +guideWidth/2, getHeight() - (getHeight() - guideHeight) / 2, mPainter);
			c.drawLine(i*iGuideStep + mGuideBorder, getHeight()/2, ((i+1)*iGuideStep) - mGuideBorder, getHeight()/2, mPainter);
			mPainter.setStrokeWidth(1f);
			mPainter.setPathEffect(new DashPathEffect(new float[]{3, 3}, 0));
			c.drawLine(i*iGuideStep + mGuideBorder, ((getHeight() - guideHeight) / 2) + guideHeight/4, ((i+1)*iGuideStep) - mGuideBorder, ((getHeight() - guideHeight) / 2) + guideHeight/4, mPainter);
			c.drawLine(i*iGuideStep + mGuideBorder, ((getHeight() - guideHeight) / 2) + 3*guideHeight/4, ((i+1)*iGuideStep) - mGuideBorder, ((getHeight() - guideHeight) / 2) + 3*guideHeight/4, mPainter);
			c.drawLine(i*iGuideStep + mGuideBorder + guideWidth/4, (getHeight() - guideHeight) / 2, i*iGuideStep + mGuideBorder + guideWidth/4, getHeight() - (getHeight() - guideHeight) / 2, mPainter);
			c.drawLine(i*iGuideStep + mGuideBorder + 3*guideWidth/4, (getHeight() - guideHeight) / 2, i*iGuideStep + mGuideBorder + 3*guideWidth/4, getHeight() - (getHeight() - guideHeight) / 2, mPainter);
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
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:
			mDrawing = true;
			mCrntX = event.getX();
			mCrntY = event.getY();
			mOldX = mCrntX;
			mOldY = mCrntY;
			break;
			
		case MotionEvent.ACTION_MOVE:
			mDrawing = true;
			mOldX = mCrntX;
			mOldY = mCrntY;
			mCrntX = event.getX();
			mCrntY = event.getY();
			break;
			
		case MotionEvent.ACTION_UP:
			mDrawing = false;
			break;
			
		default:
			mDrawing = false;
			break;	
		}
		// Refresh the view
		invalidate();
		return true;
	}
	
}
