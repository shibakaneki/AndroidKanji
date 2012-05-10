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
	private final String PART = "kvg:part";
	private final String DATA = "d";
	private final String KANJI = "kanji";
	private final String GROUP_TAG = "-g";
	private final String STROKE_TAG = "-s";
	private final String NO_VALUE = "";
	private final String POSITION = "kvg:position";
	
	private final int NOPART = 0;
	private final int NEWPART = 1;
	
	public String kvg;
	public ArrayList<KanjiVGPathInfo> pathInfo;
	private int mCurrentGroup;
	private String mCurrentElement;
	private int mCurrentPart;
	private int mCurrentStrokeIndex;
	private KanjiVGPathInfo mCurrentKVGInfo;
	private int mSubGroupNumber;
	private int mGroupStackLevel;
	
	public KanjiVGParser(){
		kvg = "";
		pathInfo = new ArrayList<KanjiVGPathInfo>();
	}
	
	public void parse(){
		try{
			mSubGroupNumber = 0;
			mCurrentElement = NO_VALUE;
			mCurrentPart = 0;
			mCurrentGroup = 0;
			mGroupStackLevel = 0;
			
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
		String element = NO_VALUE;
		
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
	
	private String getPosition(Attributes attributes){
		String element = NO_VALUE;
		
		for(int i=0; i<attributes.getLength(); i++){
			String name = attributes.getQName(i);
			String value = attributes.getValue(i);
			if(name.equals(POSITION)){
				element = value;
				break;
			}
		}
		
		return element;
	}
	
	private String getPart(Attributes attributes){
		String element = "0";
		
		for(int i=0; i<attributes.getLength(); i++){
			String name = attributes.getQName(i);
			String value = attributes.getValue(i);
			if(name.equals(PART)){
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
			if(0 < getGroup(attributes)){
				mCurrentElement = getElement(attributes);
				if(NO_VALUE != mCurrentElement){
					mCurrentPart = Integer.parseInt(getPart(attributes));
					if(NEWPART < mCurrentPart){
						for(int i=0; i<pathInfo.size(); i++){
							KanjiVGPathInfo pi = pathInfo.get(i);
							if(pi.element.equals(mCurrentKVGInfo.element)){
								mCurrentGroup = pi.group;
								break;
							}
						}
					}else{
						mGroupStackLevel++;
						if(NO_VALUE == getPosition(attributes)){
							if(1 >= mGroupStackLevel){
								mSubGroupNumber++;
								mCurrentGroup = mSubGroupNumber;
							}
						}else{
							if(2 >= mGroupStackLevel){
								mSubGroupNumber++;
								mCurrentGroup = mSubGroupNumber;
							}
						}
					}
				}
				
				
				
				
				/*//mGroupStackLevel++;
				mCurrentElement = getElement(attributes);
				if(NO_VALUE == mCurrentElement && NO_VALUE == getPosition(attributes)){
					// This group contains no element, so we will not use a specific color for it
					mCurrentGroup = 0;
				}else{
					mCurrentPart = Integer.parseInt(getPart(attributes));
					switch(mCurrentPart){
					case NOPART:
					case NEWPART:
						// This is a new group so we setup a new color for it
						if(1 >= mGroupStackLevel || NO_VALUE != getPosition(attributes)){
							mSubGroupNumber++;
							mCurrentGroup = mSubGroupNumber;
						}
						break;
					default:
						// Here we have to get the group number of this element
						for(int i=0; i<pathInfo.size(); i++){
							KanjiVGPathInfo pi = pathInfo.get(i);
							if(pi.element.equals(mCurrentKVGInfo.element)){
								mCurrentGroup = pi.group;
								break;
							}
						}
						break;
					}
					
				}*/
			}
		}else if(qName.equals(PATH)){
			mCurrentStrokeIndex = getStrokeIndex(attributes);
			mCurrentKVGInfo = new KanjiVGPathInfo();
			mCurrentKVGInfo.group = mCurrentGroup;
			mCurrentKVGInfo.index = mCurrentStrokeIndex;
			mCurrentKVGInfo.path = getPathData(attributes);
			mCurrentKVGInfo.part = mCurrentPart;
			mCurrentKVGInfo.element = mCurrentElement;
			pathInfo.add(mCurrentKVGInfo);
		}
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException{
		// If the element was a group, store it in the list
		if(qName.equals(GROUP)){
			mGroupStackLevel--;
			if(0 == mGroupStackLevel){
				mCurrentGroup = 0;
			}
		}else if(qName.equals(PATH)){
			// Nothing to do
		}else if(qName.equals(KANJI)){
			mSubGroupNumber = 0;
		}
	}
}