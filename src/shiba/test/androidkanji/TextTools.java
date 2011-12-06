package shiba.test.androidkanji;

import java.lang.Character;;

public class TextTools {
	public static int kanjiToCode(String kanji){
		int ret = 0;

		char c = kanji.charAt(0);
		
		if(kanji.length() >= 2 && Character.isSurrogatePair(kanji.charAt(0), kanji.charAt(1))){
			ret = Character.toCodePoint(kanji.charAt(0), kanji.charAt(1));
		}else{
			ret = (short)c;
		}
		
		return ret;
	}
	
	public static String codeToKanji(int code){
		String ret = "";
		
/*		if(code > 0x10000){
			
		}else{*/
			ret = new String(Character.toChars(code));
//		}
		
		return ret;
	}
}
