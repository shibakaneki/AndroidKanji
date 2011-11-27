package shiba.test.androidkanji;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.content.Context;

public class KanjiDicHelper{
	private Context mCtx;
	private final String KANJIDIC_FILE = "kanjidic2.xml";
	private final String KANJI_TAG = "character";
	private final String KANJI_CHARACTER = "literal";

	/**
	 * Constructor
	 * @param context as the given context
	 */
	public KanjiDicHelper(Context context){
		mCtx = context;
	}
	
	/**
	 * Get the list of kanji located in the dictionary
	 * @return a String list of kanji
	 */
	public String[] getAllKanji(){
		String[] res = new String[]{};
		Document doc;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
		try{
			DocumentBuilder db = dbf.newDocumentBuilder();
			
			//	Read the XML file
			InputStream input = mCtx.getAssets().open(KANJIDIC_FILE);
			InputSource iSource = new InputSource(input);
			doc = db.parse(iSource);
			
			//	Get the kanji nodes
			NodeList nodeList = doc.getElementsByTagName(KANJI_TAG);
			for(int i=0; i<nodeList.getLength(); i++){
				Node crntNode = nodeList.item(i);
				if(crntNode.getNodeType() == Node.ELEMENT_NODE){
					Element crntElem = (Element)crntNode;
					Node literalNode = crntElem.getElementsByTagName(KANJI_CHARACTER).item(0);
					if(literalNode != null){
						res[i] = literalNode.getNodeValue();
					}
				}
			}
			
		}catch(IOException e){
			System.out.println("[IOException] " + e.getMessage());
		}catch(ParserConfigurationException e){
			System.out.println("[ParserConfigurationException] " + e.getMessage());
		}catch(SAXException e){
			System.out.println("[SAXException] " + e.getMessage());
		}
		
		return res;
	}
}
