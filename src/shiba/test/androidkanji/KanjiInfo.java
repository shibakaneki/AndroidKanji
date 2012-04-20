package shiba.test.androidkanji;

import java.util.ArrayList;

public class KanjiInfo {
	public String kanji;
	public int codePoint;
	public boolean favorite;
	public String kvg;
	public int strokeCount;
	public int grade;
	public int frequency;
	public ArrayList<String> oNYomi;
	public ArrayList<String> kUNYomi;
	public int jlpt;
	
	public KanjiInfo(String kanji, boolean fav){
		this.kanji = kanji;
		this.codePoint = TextTools.kanjiToCode(kanji);
		this.favorite = fav;
		this.kvg = "";
		this.strokeCount = -1;
		this.grade = -1;
		this.frequency = -1;
		this.jlpt = -1;
	}
	
	public void toggleFavorite(){
		this.favorite = !this.favorite;
	}
}
