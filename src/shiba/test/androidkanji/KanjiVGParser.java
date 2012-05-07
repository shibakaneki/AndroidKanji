// Use: http://www.w3.org/TR/SVG/paths.html#DAttribute
package shiba.test.androidkanji;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class KanjiVGParser extends DefaultHandler{

	private final String GROUP = "g";
	private final String PATH = "path";
	private final String ELEMENT = "kvg:element";
	private final String ID = "id";
	//private final String TYPE = "kvg:type";
	//private final String PART = "kvg:part";
	private final String DATA = "d";
	private final String KANJI = "kanji";
	private final String GROUP_TAG = "-g";
	private final String STROKE_TAG = "-s";
	private final String NO_ELEMENT = "";
	
	public String kvg;
	public ArrayList<KanjiVGPathInfo> pathInfo;
	private int mCurrentGroup;
	private int mCurrentStrokeIndex;
	private KanjiVGPathInfo mCurrentKVGInfo;
	private boolean mFirstLevelGroupSet;
	private int mGroupStackLevel;
	private int mSubGroupNumber;
	private String mElement;
	
	public KanjiVGParser(){
		kvg = "";
		pathInfo = new ArrayList<KanjiVGPathInfo>();
	}
	
	public void parse(){
		try{
			mFirstLevelGroupSet = false;
			mGroupStackLevel = 0;
			mSubGroupNumber = 0;
			mElement = NO_ELEMENT;
			
			InputStream input = new ByteArrayInputStream(kvg.getBytes());
	        Reader reader = new InputStreamReader(input,"UTF-8");
			InputSource is = new InputSource(reader);
			is.setEncoding("UTF-8");
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setNamespaceAware(false);
			SAXParser parser = factory.newSAXParser();
			
			parser.parse(is, this);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void startDocument() throws SAXException{
		// Nothing to do
	}
	
	private int getGroup(Attributes attributes){
		int group = 0;
		
		for(int i=0; i<attributes.getLength(); i++){
			String name = attributes.getQName(i);
			String value = attributes.getValue(i);
			if(name.equals(ID)){
				if(value.contains(GROUP_TAG)){
					int g = Integer.parseInt(value.substring(value.indexOf(GROUP_TAG)+GROUP_TAG.length()));
					group = g;
					break;
				}
			}
		}

		return group;
	}
	
	private String getElement(Attributes attributes){
		String element = NO_ELEMENT;
		
		for(int i=0; i<attributes.getLength(); i++){
			String name = attributes.getQName(i);
			String value = attributes.getValue(i);
			if(name.equals(ELEMENT)){
				element = value;
				break;
			}
		}
		
		return element;
	}
	
	private int getStrokeIndex(Attributes attributes){
		int index = -1;
		
		for(int i=0; i<attributes.getLength(); i++){
			String name = attributes.getQName(i);
			String value = attributes.getValue(i);
			if(name.equals(ID)){
				if(value.contains(STROKE_TAG)){
					int g = Integer.parseInt(value.substring(value.indexOf(STROKE_TAG)+STROKE_TAG.length()));
					index = g;
					break;
				}
			}
		}
		
		return index;
	}
	
	private String getPathData(Attributes attributes){
		String d = "";
		
		for(int i=0; i<attributes.getLength(); i++){
			String name = attributes.getQName(i);
			String value = attributes.getValue(i);
			if(name.equals(DATA)){
				d = value;
				break;
			}
		}
		
		return d;
	}
	
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException{
		
		if(qName.equals(GROUP)){
			if(0 < getGroup(attributes)){ // Here I remove the top level group
				if(NO_ELEMENT != getElement(attributes)){
					mCurrentGroup = 0;
				}else{
					mCurrentGroup = ++ mSubGroupNumber;
				}
				
				if(!mFirstLevelGroupSet){
					mFirstLevelGroupSet = true;
				}else{
					mGroupStackLevel++;
				}
			}
/*			if(0 != getGroup(attributes)){
				if(!mFirstLevelGroupSet){
					mCurrentGroup = 0;
					mFirstLevelGroupSet = true;
				
				}else{
					mCurrentGroup = ++mSubGroupNumber;
					mGroupStackLevel++;
				}
			}*/
		}else if(qName.equals(PATH)){
			mCurrentStrokeIndex = getStrokeIndex(attributes);
			mCurrentKVGInfo = new KanjiVGPathInfo();
			mCurrentKVGInfo.group = mCurrentGroup;
			mCurrentKVGInfo.index = mCurrentStrokeIndex;
			mCurrentKVGInfo.path = getPathData(attributes);
		}
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException{
		// If the element was a group, store it in the list
		if(qName.equals(GROUP)){
			if(mGroupStackLevel > 0){
				mGroupStackLevel--;
			}
			
			if(0 == mGroupStackLevel){
				mFirstLevelGroupSet = false;
				mCurrentGroup = 0;
				mCurrentStrokeIndex = -1;
			}
		}else if(qName.equals(PATH)){
			pathInfo.add(mCurrentKVGInfo);
		}else if(qName.equals(KANJI)){
			// NOTE: 	It seems that the strokes are already in the right order in the KVG description
			//			If it is not the case, reorder the strokes here
		}
	}
}