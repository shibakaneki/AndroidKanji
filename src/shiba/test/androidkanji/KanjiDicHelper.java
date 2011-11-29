package shiba.test.androidkanji;

import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.content.Context;

public class KanjiDicHelper{
	private Context mCtx;
	private final String KANJIDIC_FILE = "kanjidic2.xml";
	
	/**
	 * Constructor
	 * @param context as the given context
	 */
	public KanjiDicHelper(Context context){
		mCtx = context;
	}
	
	/**
	 * Get the list of kanji located in the dictionary
	 * @return a list of kanji
	 */
	public ArrayList<Character> getAllKanji(){
		ArrayList<Character> kanjis = null;
		SAXParserFactory spf = SAXParserFactory.newInstance();
		
		try{
			// Create the parser
			SAXParser sp = spf.newSAXParser();
			XMLReader xr = sp.getXMLReader();
			
			// Get the datas
			KanjiDic2Handler handler = new KanjiDic2Handler();
			xr.setContentHandler(handler);
			
			InputStream input = mCtx.getAssets().open(KANJIDIC_FILE);
			InputSource iSource = new InputSource(input);
			xr.parse(iSource);
			
			kanjis = handler.getAllCharacters();
			
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		
		return kanjis;
	}
}
