package shiba.test.androidkanji;

import java.util.ArrayList;

public class KanjiInfo {
	private String mKanji;
	private int mCodePoint;
	private boolean mFavorite;
	private String mKvg;
	private int mStrokeCount;
	private int mGrade;
	private int mFrequency;
	private ArrayList<String> mONYomi;
	private ArrayList<String> mKUNYomi;
	private int mJlpt;
	
	public KanjiInfo(String kanji, boolean fav){
		mKanji = kanji;
		mCodePoint = TextTools.kanjiToCode(kanji);
		mFavorite = fav;
		mKvg = "";
		mStrokeCount = -1;
		mGrade = -1;
		mFrequency = -1;
		mJlpt = -1;
	}
	
	public int codepoint(){
		return mCodePoint;
	}
	
	public String kanji(){
		return mKanji;
	}
	
	public boolean favorite(){
		return mFavorite;
	}
	
	public void toggleFavorite(){
		mFavorite = !mFavorite;
	}
	
	public void setKVG(String kvg){
		mKvg = kvg;
	}
	
	public String kvg(){
		return mKvg;
	}
	
	public void setStrokeCount(int strokeCount){
		mStrokeCount = strokeCount;
	}
	
	public int strokeCount(){
		return mStrokeCount;
	}
	
	public void setGrade(int grade){
		mGrade = grade;
	}
	
	public int grade(){
		return mGrade;
	}
	
	public void setFrequency(int freq){
		mFrequency = freq;
	}
	
	public int frequency(){
		return mFrequency;
	}
	
	public void setONYomi(ArrayList<String> onyomi){
		mONYomi = onyomi;
	}
	
	public ArrayList<String> onYomi(){
		return mONYomi;
	}
	
	public void setKUNYomi(ArrayList<String> kunyomi){
		mKUNYomi = kunyomi;
	}
	
	public ArrayList<String> kunYomi(){
		return mKUNYomi;
	}
	
	public void setJlpt(int jlpt){
		mJlpt = jlpt;
	}
	
	public int jlpt(){
		return mJlpt;
	}
}
