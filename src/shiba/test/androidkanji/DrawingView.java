package shiba.test.androidkanji;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DrawingView extends View{

	private Context mCtx;
	private Paint mPainter;
	private Path mCrntPath;
	private int mPenColor;
	private float mPenWidth;
	private boolean mDrawing = false;
	private float mOldX;
	private float mOldY;
	private float mCrntX;
	private float mCrntY;
	private int mGuideBorder = 10;
	private int mGuideNumber = 4;
	
	public DrawingView(Context c){
		super(c);
		mCtx = c;
		initPainter();
	}
	
	public DrawingView(Context c, AttributeSet attrs){
		super(c, attrs);
		mCtx = c;
		initPainter();
	}
	
	private void initPainter(){
		mPenColor = Color.RED;
		mPenWidth = 3f;
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
		
		// Guides
		int iGuideStep = (int)(getWidth() / mGuideNumber);
		for(int i=0; i<mGuideNumber; i++){
			int guideWidth = iGuideStep - 2*mGuideBorder;
			int guideHeight = guideWidth;
			Rect guide = new Rect(	i*iGuideStep + mGuideBorder, 
									(getHeight() - guideHeight) / 2, 
									((i+1)*iGuideStep) - mGuideBorder, 
									getHeight() - (getHeight() - guideHeight) / 2);
			c.drawRect(guide, mPainter);
		}
		
		// If needed, draw the dynamic parts
		mPainter.setColor(mPenColor);
		if(mDrawing){
			c.drawLine(mOldX, mOldY, mCrntX, mCrntY, mPainter);
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
