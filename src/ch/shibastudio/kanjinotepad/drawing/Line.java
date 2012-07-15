package ch.shibastudio.kanjinotepad.drawing;

public class Line {
	public float xOrigin = 0f;
	public float yOrigin = 0f;
	public float xDest = 0f;
	public float yDest = 0f;
	
	public Line(){
		
	};
	
	public Line(float xOrigin, float yOrigin, float xDest, float yDest){
		this.xOrigin = xOrigin;
		this.yOrigin = yOrigin;
		this.xDest = xDest;
		this.yDest = yDest;
	}
}
