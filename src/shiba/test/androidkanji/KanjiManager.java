package shiba.test.androidkanji;

import java.util.ArrayList;

public class KanjiManager {

	public static KanjiInfo kanjiInfo;
	private static ArrayList<IKanjiListener> mListeners = new ArrayList<IKanjiListener>();
	
	public KanjiManager(){
		
	}

	public static void setCurrentKanji(KanjiInfo kanji){
		kanjiInfo = kanji;
		notifyKanjiChanged();
	}
	
	public static KanjiInfo kanji(){
		return kanjiInfo;
	}
	
	public static void addKanjiListener(IKanjiListener listener){
		mListeners.add(listener);
	}
	
	private static void notifyKanjiChanged(){
		for(int i=0; i<mListeners.size(); i++){
			mListeners.get(i).kanjiChanged(kanjiInfo.codePoint);
		}
	}
}
