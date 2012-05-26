package ch.shibastudio.kanjinotepad;

import android.graphics.Path;

public class KanjiStroke {
	public Path path = new Path();
	public int group;
	public boolean done;
	public float currentSegment;
	
	public KanjiStroke(){
		group = -1;
		done = false;
		currentSegment = 0;
	}
	
	public KanjiStroke(KanjiStroke stroke){
		this.group = stroke.group;
		this.path = stroke.path;
		this.done = stroke.done;
		this.currentSegment = stroke.currentSegment;
	}
}
