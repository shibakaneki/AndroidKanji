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
	
	private Context _ctx;
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
	private ArrayList<KanjiVGPathInfo> _currentKVGPaths;
	
	public KanjiCanvas(Context c){
		super(c);
		_ctx = c;
		init();
	}
	
	public KanjiCanvas(Context c, AttributeSet attrs){
		super(c, attrs);
		_ctx = c;
		init();
	}
	
	private void init(){
		_currentKVGPaths = new ArrayList<KanjiVGPathInfo>();
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
	}
	
	public void setGridVisible(boolean visible){
		_showGrid = visible;
	}
	
	@Override
	protected void onDraw(Canvas c){
		// Draw the persistent parts
		_painter.setColor(Color.BLACK);
		_painter.setAntiAlias(false);
		
		// Guides
		_painter.setStrokeWidth(3f);
		_painter.setPathEffect(null);
		int guideWidth = guideWidth();
		int guideHeight = guideWidth;
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
	
	public void setCurrentPaths(ArrayList<KanjiVGPathInfo> paths){
		_currentKVGPaths.clear();
		_currentKVGPaths = paths;
	}
}
