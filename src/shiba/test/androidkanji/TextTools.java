package shiba.test.androidkanji;

import java.lang.Character;;

public class TextTools {
	public static int kanjiToCode(String kanji){
		return Character.codePointAt(kanji.toCharArray(), 0);
	}
	
	public static String codeToKanji(int code){
		return new String(Character.toChars(code));
	}
}
