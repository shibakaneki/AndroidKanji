package ch.shibastudio.kanjinotepad;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Stack;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class KanjiVGParser extends DefaultHandler{
	
	// Tags
	private final String GROUP = "g";
	private final String PATH = "path";
	private final String KANJI = "kanji";
	
	// Attributes
	private final String ID = "id";
	private final String ELEMENT = "kvg:element";
	private final String ORIGINAL = "kvg:original";
	private final String POSITION = "kvg:position";
	private final String VARIANT = "kvg:variant";
	private final String PART = "kvg:part";
	private final String NUMBER = "kvg:number";
	private final String RADICAL = "kvg:radical";
	private final String PHON = "kvg:phon";
	private final String TRADFORM = "kvg:tradForm";
	private final String RADICALFORM = "kvg:radicalForm";
	private final String TYPE = "kvg:type";
	private final String DATA = "d";
	
	// Others
	private final String GROUP_TAG = "-g";
	private final String STROKE_TAG = "-s";
	private final String NO_VALUE = "";
	private final int NEWPART = 1;
	
	public String kvg;
	private KanjiVGPathElement mCurrentPath;
	public ArrayList<KanjiVGElement> kanjiInfo;
	private int mNextColor;
	private Stack<KanjiVGGroupElement> mGroupStack;
	
	public KanjiVGParser(){
		kvg = "";
		kanjiInfo = new ArrayList<KanjiVGElement>();
		mGroupStack = new Stack<KanjiVGGroupElement>();
	}
	
	public void parse(){
		try{
			mNextColor = 0;
			mCurrentPath = null;
			kanjiInfo.clear();
			mGroupStack.clear();
			
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
	
	private int getId(String tag, Attributes attributes){
		int id = 0;
		
		for(int i=0; i<attributes.getLength(); i++){
			String name = attributes.getQName(i);
			String value = attributes.getValue(i);
			if(name.equals(ID)){
				if(value.contains(tag)){
					int g = Integer.parseInt(value.substring(value.indexOf(tag)+tag.length()));
					id = g;
					break;
				}
			}
		}

		return id;
	}
	
	private String getAttributeValue(String tag, Attributes attributes){
		String v = NO_VALUE;
		
		for(int i=0; i<attributes.getLength(); i++){
			String name = attributes.getQName(i);
			String value = attributes.getValue(i);
			if(name.equals(tag)){
				v = value;
				break;
			}
		}
		
		return v;
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
	
	private int getParentColor(KanjiVGGroupElement parent){
		int c = 0;
		
		if(null != parent){
			if(NO_VALUE != parent.element){
				c = parent.color;
			}else{
				c = getParentColor((KanjiVGGroupElement)parent.parent);
			}
		}
		
		return c;
	}
	
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException{
		
		if(qName.equals(KANJI)){
			// Nothing to do
		}else if(qName.equals(GROUP)){
			KanjiVGGroupElement group= new KanjiVGGroupElement();
			if(!mGroupStack.isEmpty()){
				group.parent = mGroupStack.lastElement();
			}
			group.id = getId(GROUP_TAG, attributes);
			if(0 < group.id){
				group.element = getAttributeValue(ELEMENT, attributes);
				String strNumber = getAttributeValue(NUMBER, attributes);
				group.number = (strNumber.equals(NO_VALUE)) ? 0 : Integer.parseInt(strNumber);
				group.original = getAttributeValue(ORIGINAL, attributes);
				String strPart = getAttributeValue(PART, attributes);
				group.part = (strPart.equals(NO_VALUE)) ? 0 : Integer.parseInt(strPart);
				group.phon = getAttributeValue(PHON, attributes);
				group.position = getAttributeValue(POSITION, attributes);
				group.radical = getAttributeValue(RADICAL, attributes);
				group.radicalForm = getAttributeValue(RADICALFORM, attributes);
				group.tradForm = getAttributeValue(TRADFORM, attributes);
				String strVariant = getAttributeValue(VARIANT, attributes);
				group.variant = (strVariant.equals(NO_VALUE)) ? false : Boolean.parseBoolean(strVariant);
				
				// Check if this group is a part of another group. If it's the case, assign it the group color
				boolean alreadyPresent = false;
				if(NEWPART < group.part){
					for(int i=0; i<kanjiInfo.size(); i++){
						if(kanjiInfo.get(i).getClass().getName().toLowerCase().contains("group")){
							KanjiVGGroupElement g = (KanjiVGGroupElement)kanjiInfo.get(i);
							if(null != g){
								if(g.element.equals(group.element) && (g.part == (group.part - 1))){
									alreadyPresent = true;
									group.color = g.color;
									break;
								}
							}
						}
					}
					
				}
				
				// If this group is not part of another group, manage its color here
				if(!alreadyPresent){
					if(NO_VALUE != group.element/* && group.parent != null && group.parent.parent == null*/){
						int parentColor = getParentColor((KanjiVGGroupElement)group.parent);
						if(parentColor == 0){
							mNextColor++;
							group.color = mNextColor;
						}else{
							group.color = parentColor;
						}
					}else{
						group.color = group.parent.color;
					}
				}
			}else{
				group.parent = null;
				group.color = 0;
				group.element = getAttributeValue(ELEMENT, attributes);
			}
			mGroupStack.push(group);
			kanjiInfo.add(group);
		}else if(qName.equals(PATH)){
			mCurrentPath = new KanjiVGPathElement();
			if(!mGroupStack.isEmpty()){
				mCurrentPath.parent = mGroupStack.lastElement(); // Last or first?
				mCurrentPath.color = getParentColor((KanjiVGGroupElement)mCurrentPath.parent);
			}
			mCurrentPath.id = getId(STROKE_TAG, attributes);
			mCurrentPath.path = getPathData(attributes);
			mCurrentPath.type = getAttributeValue(TYPE, attributes);
			kanjiInfo.add(mCurrentPath);
		}
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException{
		// If the element was a group, store it in the list
		if(qName.equals(GROUP)){
			mGroupStack.pop();
		}else if(qName.equals(PATH)){
			// Nothing to do
		}else if(qName.equals(KANJI)){
			mNextColor = 0;
		}
	}
}