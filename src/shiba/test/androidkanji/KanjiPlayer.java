package shiba.test.androidkanji;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class KanjiPlayer extends SurfaceView implements SurfaceHolder.Callback{

	private SurfaceHolder mHolder;
	private DrawingThread mDrawingThread;
	
	public KanjiPlayer(Context context) {
		super(context);
		init();
	}
	
	public KanjiPlayer(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init(){
		mHolder = getHolder();
		mHolder.addCallback(this);
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		// TODO Auto-generated method stub
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mDrawingThread = new DrawingThread(holder);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean retry = true;
		mDrawingThread.setRunning(false);
	    while (retry) {
	        try {
	        	mDrawingThread.join();
	            retry = false;
	        } catch (InterruptedException e) {
	        }
	    }
	}

	public void flushKanjiPath(){
		// TODO: remove this method if it's not used
	}
	
	public void setCurrentPaths(ArrayList<KanjiVGElement> paths){
		// Update the paths		
		if(mDrawingThread.isAlive()){
			stopAnimation();
		}
		
		mDrawingThread.initPainting();
		mDrawingThread.generatePaths(paths);
		
		// Start the drawing thread
		mDrawingThread.setRunning(true);
		mDrawingThread.start();
	}
	
	private void stopAnimation(){
		boolean retry = true;
		mDrawingThread.setRunning(false);
	    while (retry) {
	        try {
	        	mDrawingThread.join();
	            retry = false;
	        } catch (InterruptedException e) {
	        }
	    }
	}
}
