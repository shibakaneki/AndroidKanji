package ch.shibastudio.kanjinotepad;

import java.util.ArrayList;


import android.R.color;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.SurfaceHolder;

public class DrawingThread extends Thread{
	
	private enum StrokeType{
		SEGMENT, SPACE, END;
	}
	
	private final String THREAD_NAME = "DrawingThread";
	private final String MOVETO = "M";
	private final String CURVETO = "C";
	private final String RCURVETO = "c";
	private final String SMOOTHCURVETO = "S";
	private final String RSMOOTHCURVETO = "s";
	private final int KANJIVG_ORIGIN_WIDTH = 109;
	private final int GUIDE_BORDER = 10;
	private final int SEGMENT_STEP = 5;
	private final int ANIMATION_TIME = 2; // ms
	private final int ANIMATION_WAIT = 20* ANIMATION_TIME;
	
	private SurfaceHolder mHolder;
	private boolean mRunning = true;
	private ArrayList<KanjiStroke> mKanjiPaths;
	private ArrayList<KanjiStroke> mAnimationPaths;
	private Point mOrigin;
	private float mScaleFactor;
	private Paint mPainter;
	private int mPenColor;
	private float mPenWidth;
	private int[] mColors = new int[]{Color.BLACK, Color.rgb(0x0d, 0x5b, 0xa6), Color.rgb(0xce, 0x34, 0x34), Color.rgb(0x04, 0x9a, 0x40), Color.rgb(0xe6, 0xa6, 0x00), Color.rgb(0xd2, 0x7d, 0x8e), Color.BLUE, Color.RED, Color.GREEN, Color.CYAN, Color.MAGENTA, Color.YELLOW};
	private int mGuideWidth;
	private int mGuideHeight;
	private int mWidthBorder;
	private int mHeightBorder;
	private StrokeType mLastStrokeType;
	public boolean showGrid = true;
	public boolean showKanjiShadow = true;
	public boolean showBorder = true;
	
	public DrawingThread(SurfaceHolder holder){
		mHolder = holder;
		init();
	}
	
	private void init(){
		setName(THREAD_NAME);
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
		initPainting();
	}
	
	public void initPainting(){
		mLastStrokeType = StrokeType.SEGMENT;
		mPainter.setColor(Color.BLACK);
		mPainter.setAntiAlias(false);
		
		mGuideWidth = guideWidth();
		mGuideHeight = mGuideWidth;
		mOrigin = new Point(mWidthBorder, mHeightBorder);
		mScaleFactor = (float)mGuideWidth/(float)KANJIVG_ORIGIN_WIDTH;
		
		Canvas c = mHolder.lockCanvas();
		drawDecorations(c);
		mHolder.unlockCanvasAndPost(c);
	}
	
	private int guideWidth(){
		int w = mHolder.getSurfaceFrame().width();
		int h = mHolder.getSurfaceFrame().height();
		
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
	
	public void generatePaths(ArrayList<KanjiVGElement> currentKVGPaths){
		mKanjiPaths.clear();
		for(int i=0; i<currentKVGPaths.size(); i++){
			if(currentKVGPaths.get(i).getClass().getName().toLowerCase().contains("path")){
				KanjiVGPathElement s = (KanjiVGPathElement)currentKVGPaths.get(i);
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
	
	private void drawPrettyShadow(Canvas canvas){
		mPainter.setColor(Color.BLACK);
		mPainter.setStrokeWidth(16f);
		mPainter.setPathEffect(null);
		mPainter.setAntiAlias(true);
		drawKanjiShadow(canvas);
		mPainter.setColor(Color.WHITE);
		mPainter.setStrokeWidth(12f);
		drawKanjiShadow(canvas);
		mPainter.setColor(Color.BLACK);
	}
	
	private void drawGrid(Canvas canvas){
		mPainter.setColor(Color.LTGRAY);
		mPainter.setStrokeWidth(3f);
		mPainter.setPathEffect(null);
		canvas.drawLine(mWidthBorder +mGuideWidth/2, mHeightBorder, mWidthBorder +mGuideWidth/2, mHeightBorder + mGuideHeight, mPainter);
		canvas.drawLine(mWidthBorder, mHeightBorder + mGuideHeight/2, mWidthBorder + mGuideWidth, mHeightBorder + mGuideHeight/2, mPainter);
		mPainter.setStrokeWidth(1f);
		mPainter.setPathEffect(new DashPathEffect(new float[]{3, 3}, 0));
		canvas.drawLine(mWidthBorder, mHeightBorder + mGuideHeight/4, mWidthBorder + mGuideWidth, mHeightBorder + mGuideHeight/4, mPainter);
		canvas.drawLine(mWidthBorder, mHeightBorder + 3*mGuideHeight/4, mWidthBorder + mGuideWidth, mHeightBorder + 3*mGuideHeight/4, mPainter);
		canvas.drawLine(mWidthBorder + mGuideWidth/4, mHeightBorder, mWidthBorder + mGuideWidth/4, mHeightBorder + mGuideHeight, mPainter);
		canvas.drawLine(mWidthBorder + 3*mGuideWidth/4, mHeightBorder, mWidthBorder + 3*mGuideWidth/4, mHeightBorder + mGuideHeight, mPainter);
	}
	
	private void drawBackground(Canvas canvas){
		
		mPainter.setColor(Color.parseColor("#FaF1F1"));
		mPainter.setStrokeWidth(3f);
		mPainter.setPathEffect(null);
		mPainter.setStyle(Style.FILL);
		Rect guide = new Rect(mWidthBorder, mHeightBorder, mWidthBorder + mGuideWidth, mHeightBorder + mGuideHeight);
		canvas.drawRect(guide, mPainter);
		mPainter.setStyle(Style.STROKE);
	}
	
	private void drawBorder(Canvas canvas){
		mPainter.setColor(Color.BLACK);
		mPainter.setStrokeWidth(3f);
		mPainter.setPathEffect(null);
		Rect guide = new Rect(mWidthBorder, mHeightBorder, mWidthBorder + mGuideWidth, mHeightBorder + mGuideHeight);
		canvas.drawRect(guide, mPainter);
	}
	
	private void drawKanjiShadow(Canvas canvas){
		for(int i=0; i<mKanjiPaths.size(); i++){
			canvas.drawPath(mKanjiPaths.get(i).path, mPainter);
		}
	}
	
	private void drawDecorations(Canvas canvas){
		if(null != canvas){
			canvas.drawColor(Color.WHITE);
			drawBackground(canvas);
			if(showGrid){
				drawGrid(canvas);
			}
			
			if(showBorder){
				drawBorder(canvas);
			}
			
			if(showKanjiShadow){
				drawPrettyShadow(canvas);
			}
		}
	}
	
	private void drawAnimatedSegments(Canvas canvas){
		for(int i=0; i<mAnimationPaths.size(); i++){
			KanjiStroke stroke = mAnimationPaths.get(i);
			mPainter.setColor(mColors[stroke.group]);
			mPainter.setStrokeWidth(13f);
			canvas.drawPath(stroke.path, mPainter);
		}
	}
	
	private StrokeType generateNextSegment(){
		StrokeType retCode = StrokeType.SEGMENT;
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
					retCode = StrokeType.SPACE;
				}else{
					stroke.currentSegment = endOfSegment;
					retCode = StrokeType.SEGMENT;
				}
				return retCode;
			}else{
				KanjiStroke fullStroke = new KanjiStroke(stroke);
				mAnimationPaths.add(fullStroke);
			}
		}
		retCode = StrokeType.END;
		return retCode;
	}
	
	@Override
	public void run(){
		while(mRunning){
	        Canvas canvas = null;
	        try{
	        	canvas = mHolder.lockCanvas();
	        	synchronized (mHolder){
	        		drawDecorations(canvas);
	        		drawAnimatedSegments(canvas);
	        		
	        		switch(mLastStrokeType){
	        		case SEGMENT:
	        			sleep(ANIMATION_TIME);
	        			break;
	        		case SPACE:
	        			sleep(ANIMATION_WAIT);
	        			break;
	        		case END:
	        			mRunning = false;
	        			break;
	        		}
	        		
	        		mLastStrokeType = generateNextSegment();
				}
	        } catch (InterruptedException e) {
				e.printStackTrace();
			}finally{
	        	if(null != canvas){
	        		mHolder.unlockCanvasAndPost(canvas);
	        	}
	        }
	    }
	}
	
	public void setRunning(boolean running){
		mRunning = running;
	}
}
