package shiba.test.androidkanji;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
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
	
	private Paint _painter;
	private int _penColor;
	private float _penWidth;
	private boolean _drawing = false;
	private float _oldX;
	private float _oldY;
	private float _crntX;
	private float _crntY;
	private ArrayList<Line> _lines = new ArrayList<Line>();
	private final int GUIDE_BORDER = 10;
	private int _widthBorder;
	private int _heightBorder;
	private KCMode _mode = KCMode.DRAWING;
	private boolean _showGrid = true;
	private boolean _showKanjiShadow = false;
	private ArrayList<KanjiVGPathInfo> _currentKVGPaths;
	private ArrayList<Path> _kanjiPaths;
	private Point _origin;
	private float _scaleFactor;
	
	public KanjiCanvas(Context c){
		super(c);
		init();
	}
	
	public KanjiCanvas(Context c, AttributeSet attrs){
		super(c, attrs);
		init();
	}
	
	private void init(){
		_currentKVGPaths = new ArrayList<KanjiVGPathInfo>();
		_kanjiPaths = new ArrayList<Path>();
		
		_penColor = Color.BLUE;
		_penWidth = 7f;
		_painter = new Paint();
		_painter.setDither(true);
		_painter.setColor(_penColor);
		_painter.setStyle(Paint.Style.STROKE);
		_painter.setStrokeJoin(Paint.Join.ROUND);
		_painter.setStrokeCap(Paint.Cap.ROUND);
		_painter.setStrokeWidth(_penWidth);
	}
	
	public void setMode(KCMode mode){
		_mode = mode;
		switch(mode){
		case ANIMATION:
			_showKanjiShadow = true;
			break;
		case DRAWING:
			_showKanjiShadow = false;
			break;
		}
	}
	
	public void setGridVisible(boolean visible){
		_showGrid = visible;
	}
	
	public void setKanjiShadowVisible(boolean visible){
		_showKanjiShadow = visible;
	}
	
	@Override
	protected void onDraw(Canvas c){
		// Draw the persistent parts
		_painter.setColor(Color.BLACK);
		_painter.setAntiAlias(false);
		
		int guideWidth = guideWidth();
		int guideHeight = guideWidth;
		_origin = new Point(_widthBorder, _heightBorder);
		_scaleFactor = (float)guideWidth/(float)KANJIVG_ORIGIN_WIDTH;
		
		if(_showKanjiShadow){
			_painter.setColor(Color.BLACK);
			_painter.setStrokeWidth(16f);
			_painter.setPathEffect(null);
			_painter.setAntiAlias(true);
			drawKanjiShadow(c);
			_painter.setColor(Color.WHITE);
			_painter.setStrokeWidth(14f);
			drawKanjiShadow(c);
			_painter.setColor(Color.BLACK);
		}
		 
		// Guides
		_painter.setStrokeWidth(3f);
		_painter.setPathEffect(null);
		Rect guide = new Rect(_widthBorder, _heightBorder, _widthBorder + guideWidth, _heightBorder + guideHeight);
		c.drawRect(guide, _painter);
		
		if(_showGrid){
			c.drawLine(_widthBorder +guideWidth/2, _heightBorder, _widthBorder +guideWidth/2, _heightBorder + guideHeight, _painter);
			c.drawLine(_widthBorder, _heightBorder + guideHeight/2, _widthBorder + guideWidth, _heightBorder + guideHeight/2, _painter);
			_painter.setStrokeWidth(1f);
			_painter.setPathEffect(new DashPathEffect(new float[]{3, 3}, 0));
			c.drawLine(_widthBorder, _heightBorder + guideHeight/4, _widthBorder + guideWidth, _heightBorder + guideHeight/4, _painter);
			c.drawLine(_widthBorder, _heightBorder + 3*guideHeight/4, _widthBorder + guideWidth, _heightBorder + 3*guideHeight/4, _painter);
			c.drawLine(_widthBorder + guideWidth/4, _heightBorder, _widthBorder + guideWidth/4, _heightBorder + guideHeight, _painter);
			c.drawLine(_widthBorder + 3*guideWidth/4, _heightBorder, _widthBorder + 3*guideWidth/4, _heightBorder + guideHeight, _painter);
		}
		
		_painter.setColor(_penColor);
		_painter.setPathEffect(null);
		_painter.setStrokeWidth(_penWidth);
		_painter.setAntiAlias(true);
		
		// If needed, add new user lines
		if(_drawing){
			_lines.add(new Line(_oldX, _oldY, _crntX, _crntY));
		}
		
		// Draw the lines done by the user
		for(int i=0; i<_lines.size(); i++){
			Line l = _lines.get(i);
			c.drawLine(l.xOrigin, l.yOrigin, l.xDest, l.yDest, _painter);
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:
			if(KCMode.DRAWING == _mode && isInGuide(x, y)){
				_drawing = true;
				_crntX = event.getX();
				_crntY = event.getY();
				_oldX = _crntX;
				_oldY = _crntY;
			}
			break;
			
		case MotionEvent.ACTION_MOVE:
			if(KCMode.DRAWING == _mode && isInGuide(x, y)){
				_drawing = true;
				_oldX = _crntX;
				_oldY = _crntY;
				_crntX = event.getX();
				_crntY = event.getY();
			}
			break;
			
		case MotionEvent.ACTION_UP:
			if(KCMode.DRAWING == _mode && isInGuide(x, y)){
				_drawing = false;
			}
			break;
			
		default:
			if(KCMode.DRAWING == _mode && isInGuide(x, y)){
				_drawing = false;
			}
			break;	
		}
		// Refresh the view
		invalidate();
		return true;
	}
	
	private boolean isInGuide(float x, float y){
		int guideHeight = guideWidth();
		if(x >= _widthBorder && x <= _widthBorder + guideWidth() && y >= _heightBorder && y <= _heightBorder + guideHeight){
			return true;
		}
		return false;
	}
	
	private int guideWidth(){
		int w = getWidth();
		int h = getHeight();
		
		if(w < h){
			_widthBorder = GUIDE_BORDER;
			_heightBorder = (h-w)/2;
			return w - 2*_widthBorder;
		}else{
			_heightBorder = GUIDE_BORDER;
			_widthBorder = (w-h)/2;
			return h - 2*_heightBorder;
		}
	}
	
	public void flushKanjiPath(){
		_currentKVGPaths.clear();
	}
	
	public void setCurrentPaths(ArrayList<KanjiVGPathInfo> paths){
		_currentKVGPaths = paths;
		generatePaths();
		invalidate();
	}
	
	private void generatePaths(){
		_kanjiPaths.clear();
		for(int i=0; i<_currentKVGPaths.size(); i++){
			Path path = new Path();
			float x = 0f;	// Destination x coordinate
			float y = 0f;	// Destination y coordinate
			float x1 = 0f;	// Start control point x coordinate
			float y1 = 0f;	// Start control point y coordinate
			float x2 = 0f;	// End control point x coordinate
			float y2 = 0f;	// End control point y coordinate
			String desc = _currentKVGPaths.get(i).path;
			String prevCmd = "";
			
			while(!desc.isEmpty()){
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
						x = _origin.x + Float.parseFloat(coord.get(0)) * _scaleFactor;
						y = _origin.y + Float.parseFloat(coord.get(1)) * _scaleFactor;
						path.moveTo(x, y);
					}
				}else if(cmd.equals(CURVETO) || cmd.equals(RCURVETO)){
					if(6 <= coord.size()){
						float xOffset = (cmd.equals(RCURVETO)) ? 0.0f : _origin.x;
						float yOffset = (cmd.equals(RCURVETO)) ? 0.0f : _origin.y;
						float xPrev = x;
						float yPrev = y;
						x1 = xOffset + Float.parseFloat(coord.get(0)) * _scaleFactor;
						y1 = yOffset + Float.parseFloat(coord.get(1)) * _scaleFactor;
						x2 = xOffset + Float.parseFloat(coord.get(2)) * _scaleFactor;
						y2 = yOffset + Float.parseFloat(coord.get(3)) * _scaleFactor;
						x = xOffset + Float.parseFloat(coord.get(4)) * _scaleFactor;
						y = yOffset + Float.parseFloat(coord.get(5)) * _scaleFactor;
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
						x2 = _origin.x + Float.parseFloat(coord.get(0)) * _scaleFactor;
						y2 = _origin.y + Float.parseFloat(coord.get(1)) * _scaleFactor;
						x = _origin.x + Float.parseFloat(coord.get(2)) * _scaleFactor;
						y = _origin.y + Float.parseFloat(coord.get(3)) * _scaleFactor;
						
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
						
						x2 = x + Float.parseFloat(coord.get(0)) * _scaleFactor;
						y2 = y + Float.parseFloat(coord.get(1)) * _scaleFactor;
						x = x + Float.parseFloat(coord.get(2)) * _scaleFactor;
						y = y + Float.parseFloat(coord.get(3)) * _scaleFactor;
						
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
			_kanjiPaths.add(path);
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
		while(!remaining.isEmpty()){
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
	
	private void drawKanjiShadow(Canvas c){
		for(int i=0; i<_kanjiPaths.size(); i++){
			c.drawPath(_kanjiPaths.get(i), _painter);
		}
	}
}

