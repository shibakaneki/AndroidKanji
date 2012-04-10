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
	//private final String ELEMENT = "kvg:element";
	private final String ID = "id";
	//private final String TYPE = "kvg:type";
	//private final String PART = "kvg:part";
	private final String DATA = "d";
	private final String KANJI = "kanji";
	private final String GROUP_TAG = "-g";
	private final String STROKE_TAG = "-s";
	
	private String _kvg;
	private ArrayList<KanjiVGPathInfo> _pathInfo;
	private int _currentGroup;
	private int _currentStrokeIndex;
	private KanjiVGPathInfo _currentKVGInfo;
	private boolean _firstLevelGroupSet;
	private int _groupStackLevel;
	
	public KanjiVGParser(){
		_kvg = "";
		_pathInfo = new ArrayList<KanjiVGPathInfo>();
	}
	
	public void setCurrentKVG(String kvg){
		_kvg = kvg;
	}
	
	public String kvg(){
		return _kvg;
	}
	
	public ArrayList<KanjiVGPathInfo> paths(){
		return _pathInfo;
	}
	
	public void parse(){
		try{
			_firstLevelGroupSet = false;
			_groupStackLevel = 0;
			
			InputStream input = new ByteArrayInputStream(_kvg.getBytes());
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
		int group = -1;
		
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
			if(!_firstLevelGroupSet){
				int group = getGroup(attributes);
				if(0 <= group){
					_currentGroup = group;
					_firstLevelGroupSet = true;
				}
			}
			_groupStackLevel++;
		}else if(qName.equals(PATH)){
			_currentStrokeIndex = getStrokeIndex(attributes);
			_currentKVGInfo = new KanjiVGPathInfo();
			_currentKVGInfo.group = _currentGroup;
			_currentKVGInfo.index = _currentStrokeIndex;
			_currentKVGInfo.path = getPathData(attributes);
		}
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException{
		// If the element was a group, store it in the list
		if(qName.equals(GROUP)){
			_groupStackLevel--;
			if(0 == _groupStackLevel){
				_firstLevelGroupSet = false;
				_currentGroup = -1;
				_currentStrokeIndex = -1;
			}
		}else if(qName.equals(PATH)){
			_pathInfo.add(_currentKVGInfo);
		}else if(qName.equals(KANJI)){
			// NOTE: 	It seems that the strokes are already in the right order in the KVG description
			//			If it is not the case, reorder the strokes here
		}
	}
}