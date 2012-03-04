package shiba.test.androidkanji;

public class KanjiInfo {
	private String mKanji;
	private boolean mFavorite;
	
	public KanjiInfo(String kanji, boolean fav){
		mKanji = kanji;
		mFavorite = fav;
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
}
