package ch.shibastudio.kanjinotepad;

import java.util.ArrayList;

public class KanjiManager {

	public static KanjiInfo kanjiInfo;
	private static ArrayList<IKanjiListener> mListeners = new ArrayList<IKanjiListener>();
	public static boolean phoneMode = false;
	
	public KanjiManager(){
		
	}

	public static void setCurrentKanji(KanjiInfo kanji){
		if(phoneMode){
			KNotepadPhoneActivity.tabHost.setCurrentTab(1);
		}
		
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
