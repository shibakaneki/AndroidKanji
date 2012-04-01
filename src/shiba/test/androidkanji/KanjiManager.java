package shiba.test.androidkanji;

public class KanjiManager {

	private static KanjiInfo mKanji;
	
	public KanjiManager(){
		
	}
	
	public static void setCurrentKanji(KanjiInfo kanji){
		mKanji = kanji;
		// TODO: Send an intent to notify everyone that the kanji has changed
		
	}
	
	public static KanjiInfo kanji(){
		return mKanji;
	}
}
