package shiba.test.androidkanji;

import java.util.ArrayList;

public class KanjiManager {

	private static int mCodePoint;
	private static ArrayList<String> mONYomi;
	private static ArrayList<String> mKUNYomi;
	private static int mJLPT;
	
	public KanjiManager(){
		
	}
	
	void setCurrentKanji(int cp, ArrayList<String> on, ArrayList<String> kun, int jlpt){
		mCodePoint = cp;
		mONYomi = on;
		mKUNYomi = kun;
		mJLPT = jlpt;
	}
	
	String kanji(){
		return TextTools.codeToKanji(mCodePoint);
	}
	
	int codePoint(){
		return mCodePoint;
	}
	
	ArrayList<String> onYomi(){
		return mONYomi;
	}
	
	ArrayList<String> kunYomi(){
		return mKUNYomi;
	}
	
	int jlpt()
	{
		return mJLPT;
	}
}
