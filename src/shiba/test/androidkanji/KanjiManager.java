package shiba.test.androidkanji;

import java.util.ArrayList;

public class KanjiManager {

	private static KanjiInfo mKanji;
	private static ArrayList<IKanjiListener> mListeners = new ArrayList<IKanjiListener>();
	
	public KanjiManager(){
		
	}

	public static void setCurrentKanji(KanjiInfo kanji){
		mKanji = kanji;
		// TODO: Send an intent to notify everyone that the kanji has changed
		notifyKanjiChanged();
	}
	
	public static KanjiInfo kanji(){
		return mKanji;
	}
	
	public static void addKanjiListener(IKanjiListener listener){
		mListeners.add(listener);
	}
	
	private static void notifyKanjiChanged(){
		for(int i=0; i<mListeners.size(); i++){
			mListeners.get(i).kanjiChanged(mKanji.codepoint());
		}
	}
}
