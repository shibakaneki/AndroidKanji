package ch.shibastudio.kanjinotepad.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.Character;
import java.util.Scanner;

public class TextTools {
	public static int kanjiToCode(String kanji){
		return Character.codePointAt(kanji.toCharArray(), 0);
	}
	
	public static String codeToKanji(int code){
		return new String(Character.toChars(code));
	}
	
	public static String fileToText(String filename, String encoding) throws IOException{
		StringBuilder text = new StringBuilder();
	    String NL = System.getProperty("line.separator");
	    Scanner scanner = new Scanner(new FileInputStream(filename), encoding);
	    try {
	      while (scanner.hasNextLine()){
	        text.append(scanner.nextLine() + NL);
	      }
	    }
	    finally{
	      scanner.close();
	    }
	    return text.toString();
	}
	
	public static void textToFile(String text, String filename, String encoding) throws IOException{
		Writer out = new OutputStreamWriter(new FileOutputStream(filename), encoding);
	    try {
	      out.write(text);
	    }
	    finally {
	      out.close();
	    }
	}
	
	public static String stripInvalidXMLChars(String xml){
		StringBuffer out = new StringBuffer(); // Used to hold the output.
        char current; // Used to reference the current character.

        if (xml == null || ("".equals(xml))) return ""; // vacancy test.
        for (int i = 0; i < xml.length(); i++) {
            current = xml.charAt(i); // NOTE: No IndexOutOfBoundsException caught here; it should not happen.
            if ((current == 0x9) ||
                (current == 0xA) ||
                (current == 0xD) ||
                ((current >= 0x20) && (current <= 0xD7FF)) ||
                ((current >= 0xE000) && (current <= 0xFFFD)) ||
                ((current >= 0x10000) && (current <= 0x10FFFF)))
                out.append(current);
        }
        return out.toString();
	}
}
