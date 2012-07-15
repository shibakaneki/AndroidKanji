package ch.shibastudio.kanjinotepad;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class KanjiCanvas extends View{

	public enum KCMode{
		DRAWING, ANIMATION
	}
	
	private final String MOVETO = "M";
	private final String CURVETO = "C";
	private final String RCURVETO = "c";
	private final String SMOOTHCURVETO = "S";
	private final String RSMOOTHCURVETO = "s";
	private final int KANJIVG_ORIGIN_WIDTH = 109;
	private final int SEGMENT_STEP = 200;
	//private final int ANIMATION_TIME = 100; // ms
	private final int GUIDE_BORDER = 10;
	
	private Paint mPainter;
	private int mPenColor;
	private float mPenWidth;
	private boolean mDrawing = false;
	private float mOldX;
	private float mOldY;
	private float mCrntX;
	private float mCrntY;
	private ArrayList<Line> mLines = new ArrayList<Line>();
	private int mWidthBorder;
	private int mHeightBorder;
	private KCMode mMode = KCMode.DRAWING;
	private boolean mShowGrid = true;
	private boolean mShowKanjiShadow = false;
	private boolean mShowBorder = true;
	private ArrayList<KanjiVGElement> mCurrentKVGPaths;
	private ArrayList<KanjiStroke> mKanjiPaths;
	private Point mOrigin;
	private float mScaleFactor;
	private int[] mColors = new int[]{Color.BLACK, Color.rgb(0x0d, 0x5b, 0xa6), Color.rgb(0xce, 0x34, 0x34), Color.rgb(0x04, 0x9a, 0x40), Color.rgb(0xe6, 0xa6, 0x00), Color.rgb(0xd2, 0x7d, 0x8e), Color.BLUE, Color.RED, Color.GREEN, Color.CYAN, Color.MAGENTA, Color.YELLOW};
	private Canvas mCanvas = null;
	private int mGuideWidth;
	private int mGuideHeight;
	private boolean mAnimationInProgress = false;
	private ArrayList<KanjiStroke> mAnimationPaths;
	
	public KanjiCanvas(Context c){
		super(c);
		init();
	}
	
	public KanjiCanvas(Context c, AttributeSet attrs){
		super(c, attrs);
		init();
	}
	
	private void init(){
		mColors[0] = Color.BLACK;
		mCurrentKVGPaths = new ArrayList<KanjiVGElement>();
		mKanjiPaths = new ArrayList<KanjiStroke>();
		mAnimationPaths = new ArrayList<KanjiStroke>();
		mPenColor = Color.BLACK;
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
		switch(mode){
		case ANIMATION:
			mShowKanjiShadow = true;
			break;
		case DRAWING:
			mShowKanjiShadow = false;
			break;
		}
	}
	
	public void setGridVisible(boolean visible){
		mShowGrid = visible;
	}
	
	public void setKanjiShadowVisible(boolean visible){
		mShowKanjiShadow = visible;
	}
	
	@Override
	protected void onDraw(Canvas c){
		mCanvas = c;
		
		initPainting();
		
		drawBackground(c);
		
		if(mShowGrid){
			drawGrid();
		}
		
		if(mShowBorder){
			drawBorder();
		}
		
		if(mShowKanjiShadow){
			drawPrettyShadow();
		}
		
		if(KCMode.DRAWING == mMode){
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
				mCanvas.drawLine(l.xOrigin, l.yOrigin, l.xDest, l.yDest, mPainter);
			}
		}else if(KCMode.ANIMATION == mMode){
			drawAnimatedSegments();
		}

	}
	
	private void drawBackground(Canvas canvas){
		mPainter.setColor(Color.parseColor("#FFFCEC"));
		mPainter.setStrokeWidth(3f);
		mPainter.setPathEffect(null);
		mPainter.setStyle(Style.FILL);
		Rect guide = new Rect(mWidthBorder, mHeightBorder, mWidthBorder + mGuideWidth, mHeightBorder + mGuideHeight);
		canvas.drawRect(guide, mPainter);
		mPainter.setStyle(Style.STROKE);
	}

	private void drawPrettyShadow(){
		mPainter.setColor(Color.BLACK);
		mPainter.setStrokeWidth(16f);
		mPainter.setPathEffect(null);
		mPainter.setAntiAlias(true);
		drawKanjiShadow();
		mPainter.setColor(Color.WHITE);
		mPainter.setStrokeWidth(12f);
		drawKanjiShadow();
		mPainter.setColor(Color.BLACK);
	}
	
	private void drawGrid(){
		mPainter.setColor(Color.LTGRAY);
		mPainter.setStrokeWidth(3f);
		mPainter.setPathEffect(null);
		mCanvas.drawLine(mWidthBorder +mGuideWidth/2, mHeightBorder, mWidthBorder +mGuideWidth/2, mHeightBorder + mGuideHeight, mPainter);
		mCanvas.drawLine(mWidthBorder, mHeightBorder + mGuideHeight/2, mWidthBorder + mGuideWidth, mHeightBorder + mGuideHeight/2, mPainter);
		mPainter.setStrokeWidth(1f);
		mPainter.setPathEffect(new DashPathEffect(new float[]{3, 3}, 0));
		mCanvas.drawLine(mWidthBorder, mHeightBorder + mGuideHeight/4, mWidthBorder + mGuideWidth, mHeightBorder + mGuideHeight/4, mPainter);
		mCanvas.drawLine(mWidthBorder, mHeightBorder + 3*mGuideHeight/4, mWidthBorder + mGuideWidth, mHeightBorder + 3*mGuideHeight/4, mPainter);
		mCanvas.drawLine(mWidthBorder + mGuideWidth/4, mHeightBorder, mWidthBorder + mGuideWidth/4, mHeightBorder + mGuideHeight, mPainter);
		mCanvas.drawLine(mWidthBorder + 3*mGuideWidth/4, mHeightBorder, mWidthBorder + 3*mGuideWidth/4, mHeightBorder + mGuideHeight, mPainter);
	}
	
	private void drawBorder(){
		mPainter.setColor(Color.BLACK);
		mPainter.setStrokeWidth(3f);
		mPainter.setPathEffect(null);
		Rect guide = new Rect(mWidthBorder, mHeightBorder, mWidthBorder + mGuideWidth, mHeightBorder + mGuideHeight);
		mCanvas.drawRect(guide, mPainter);
	}
	
	private void initPainting(){
		mPainter.setColor(Color.BLACK);
		mPainter.setAntiAlias(false);
		
		mGuideWidth = guideWidth();
		mGuideHeight = mGuideWidth;
		mOrigin = new Point(mWidthBorder, mHeightBorder);
		mScaleFactor = (float)mGuideWidth/(float)KANJIVG_ORIGIN_WIDTH;
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
	
	
	public void flushKanjiPath(){
		mCurrentKVGPaths.clear();
	}
	
	
	public void setCurrentPaths(ArrayList<KanjiVGElement> paths){
		mCurrentKVGPaths = paths;
		generatePaths();
		invalidate();
	}
	
	
	private void generatePaths(){
		mKanjiPaths.clear();
		for(int i=0; i<mCurrentKVGPaths.size(); i++){
			if(mCurrentKVGPaths.get(i).getClass().getName().toLowerCase().contains("path")){
				KanjiVGPathElement s = (KanjiVGPathElement)mCurrentKVGPaths.get(i);
				if(null != s){
					KanjiStroke stroke = new KanjiStroke();
					stroke.group = s.color;
					Path path = new Path();
					float x = 0f;	// Destination x coordinate
					float y = 0f;	// Destination y coordinate
					float x1 = 0f;	// Start control point x coordinate
					float y1 = 0f;	// Start control point y coordinate
					float x2 = 0f;	// End control point x coordinate
					float y2 = 0f;	// End control point y coordinate
					String desc = s.path;
					String prevCmd = "";
					
					while(!(desc.length() == 0)){
						// Initialize indexes
						int nextBlockIndex = desc.length() - 1;
						
						int[] indexes = new int[4];
						
						indexes[0] = desc.indexOf(CURVETO, 1);
						indexes[1] = desc.indexOf(RCURVETO, 1);
						indexes[2] = desc.indexOf(SMOOTHCURVETO, 1);
						indexes[3] = desc.indexOf(RSMOOTHCURVETO, 1);
						
						for(int j=0; j<indexes.length; j++){
							if(0 <= indexes[j] && indexes[j] < nextBlockIndex){
								nextBlockIndex = indexes[j];
							}
						}
						
						// Get the coordinates
						String block = "";
						if(nextBlockIndex == desc.length() - 1){
							block = desc.substring(0, nextBlockIndex + 1);
						}else{
							block = desc.substring(0, nextBlockIndex);
						}
						
						String cmd = "" +desc.charAt(0);
						ArrayList<String> coord = new ArrayList<String>();
						generateCoordinates(block, coord);
						
						// Build the path
						if(cmd.equals(MOVETO)){
							if(2 <= coord.size()){
								x = mOrigin.x + Float.parseFloat(coord.get(0)) * mScaleFactor;
								y = mOrigin.y + Float.parseFloat(coord.get(1)) * mScaleFactor;
								path.moveTo(x, y);
							}
						}else if(cmd.equals(CURVETO) || cmd.equals(RCURVETO)){
							if(6 <= coord.size()){
								float xOffset = (cmd.equals(RCURVETO)) ? 0.0f : mOrigin.x;
								float yOffset = (cmd.equals(RCURVETO)) ? 0.0f : mOrigin.y;
								float xPrev = x;
								float yPrev = y;
								x1 = xOffset + Float.parseFloat(coord.get(0)) * mScaleFactor;
								y1 = yOffset + Float.parseFloat(coord.get(1)) * mScaleFactor;
								x2 = xOffset + Float.parseFloat(coord.get(2)) * mScaleFactor;
								y2 = yOffset + Float.parseFloat(coord.get(3)) * mScaleFactor;
								x = xOffset + Float.parseFloat(coord.get(4)) * mScaleFactor;
								y = yOffset + Float.parseFloat(coord.get(5)) * mScaleFactor;
								if(cmd.equals(RCURVETO)){
									path.rCubicTo(x1, y1, x2, y2, x, y);
									x1 += xPrev;
									y1 += yPrev;
									x2 += xPrev;
									y2 += yPrev;
									x += xPrev;
									y += yPrev;
								}else{
									path.cubicTo(x1, y1, x2, y2, x, y);
								}
							}
						}else if(cmd.equals(SMOOTHCURVETO)){
							if(4 <= coord.size()){
								if(prevCmd.equals(CURVETO) || prevCmd.equals(RCURVETO) || prevCmd.equals(RSMOOTHCURVETO) || prevCmd.equals(SMOOTHCURVETO)){
									x1 = generateSmoothX1(x, x2);
									y1 = generateSmoothY1(y, y2);
								}else{
									x1 = x;
									y1 = x;
								}
								x2 = mOrigin.x + Float.parseFloat(coord.get(0)) * mScaleFactor;
								y2 = mOrigin.y + Float.parseFloat(coord.get(1)) * mScaleFactor;
								x = mOrigin.x + Float.parseFloat(coord.get(2)) * mScaleFactor;
								y = mOrigin.y + Float.parseFloat(coord.get(3)) * mScaleFactor;
								
								path.cubicTo(x1, y1, x2, y2, x, y);
							}
						}else if(cmd.equals(RSMOOTHCURVETO)){
							if(4 <= coord.size()){
								if(prevCmd.equals(CURVETO) || prevCmd.equals(RCURVETO) || prevCmd.equals(RSMOOTHCURVETO) || prevCmd.equals(SMOOTHCURVETO)){
									x1 = generateSmoothX1(x, x2);
									y1 = generateSmoothY1(y, y2);
								}else{
									x1 = x;
									y1 = x;
								}
								
								x2 = x + Float.parseFloat(coord.get(0)) * mScaleFactor;
								y2 = y + Float.parseFloat(coord.get(1)) * mScaleFactor;
								x = x + Float.parseFloat(coord.get(2)) * mScaleFactor;
								y = y + Float.parseFloat(coord.get(3)) * mScaleFactor;
								
								path.cubicTo(x1, y1, x2, y2, x, y);
							}
						}
						
						prevCmd = cmd;
						
						if(nextBlockIndex == desc.length()-1){
							desc = "";
						}else{
							desc = desc.substring(nextBlockIndex);
						}
					}
					
					// Store the generated path
					stroke.path = path;
					mKanjiPaths.add(stroke);
				}
			}
		}
	}
	
	
	private float generateSmoothX1(float xPrev, float xPrevCtrl){
		float x = 0.0f;
		
		x = (2 * xPrev) - xPrevCtrl;

		return x;
	}
	
	
	private float generateSmoothY1(float yPrev, float yPrevCtrl){
		float y = 0.0f;
		
		y = (2 * yPrev) - yPrevCtrl;
		
		return y;
	}
	
	
	private void generateCoordinates(String block, ArrayList<String> coords){
		// First, remove the command
		String remaining = block.substring(1);
		
		// Then get the coordinates
		while(!(remaining.length() == 0)){
			int nextCoordIndex = remaining.length();
			int nextSeparator = remaining.indexOf(",", 1);
			int nextMinus = remaining.indexOf("-", 1);
			
			if(nextSeparator >= 0 && nextMinus >= 0){
				nextCoordIndex = Math.min(nextSeparator, nextMinus);
			}else if(nextSeparator >= 0){
				nextCoordIndex = nextSeparator;
			}else if(nextMinus >= 0){
				nextCoordIndex = nextMinus;
			}
			
			coords.add(remaining.substring(0, nextCoordIndex));
			
			if(nextCoordIndex == nextMinus){
				remaining = remaining.substring(nextCoordIndex);
			}else if(nextCoordIndex == remaining.length()){
				remaining = remaining.substring(nextCoordIndex);
			}else{
				remaining = remaining.substring(nextCoordIndex + 1);
			}
		}
	}
	
	
	private void drawKanjiShadow(){
		for(int i=0; i<mKanjiPaths.size(); i++){
			mCanvas.drawPath(mKanjiPaths.get(i).path, mPainter);
		}
	}
	
	private void drawAnimatedSegments(){
		for(int i=0; i<mAnimationPaths.size(); i++){
			KanjiStroke stroke = mAnimationPaths.get(i);
			mPainter.setColor(mColors[stroke.group]);
			mPainter.setStrokeWidth(13f);
			mCanvas.drawPath(stroke.path, mPainter);
		}
	}
	
	private void generateNextSegment(){
		mAnimationPaths.clear();
		for(int i=0; i<mKanjiPaths.size(); i++){
			KanjiStroke stroke = mKanjiPaths.get(i);
			
			if(!stroke.done){
				KanjiStroke incompleteStroke = new KanjiStroke();
				incompleteStroke.group = stroke.group;
				PathMeasure pm = new PathMeasure();
				pm.setPath(stroke.path, false);				
				float endOfSegment = (stroke.currentSegment + SEGMENT_STEP < pm.getLength()) ? stroke.currentSegment + SEGMENT_STEP : pm.getLength();
				pm.getSegment(0f, endOfSegment, incompleteStroke.path, true);

				mAnimationPaths.add(incompleteStroke);
				
				if(endOfSegment == pm.getLength()){
					stroke.done = true;
				}else{
					stroke.currentSegment = endOfSegment;
				}
				return;
			}else{
				KanjiStroke fullStroke = new KanjiStroke(stroke);
				mAnimationPaths.add(fullStroke);
			}
		}
		mAnimationInProgress = false;
	}
	
	public void startAnimation(){
		if(mAnimationInProgress){
			pauseAnimation();
			for(int i=0; i<mKanjiPaths.size(); i++){
				KanjiStroke stroke = mKanjiPaths.get(i);
				stroke.done = false;
				stroke.currentSegment = 0f;
			}
		}
		mAnimationInProgress = true;
		
		// Test
		while(mAnimationInProgress){
			generateNextSegment();
		}
		// Test
		
	}
	
	public void pauseAnimation(){
		mAnimationInProgress = false;
	}
	
	public void nextStroke(){
		
	}
	
	public void previousStroke(){
		
	}
	
	public void clearCanvas(){
		if(KCMode.DRAWING == mMode){
			mLines.clear();
		}
	}
}