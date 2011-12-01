package shiba.test.androidkanji;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class KanjiDic2Handler extends DefaultHandler {
	// -----------------------------------------------------------------------------------
	// TODO: This class should use an ArrayList<Character> instead of an ArrayList<String>
	// -----------------------------------------------------------------------------------

	private final String LITERAL = "literal";
	private final String JLPT = "jlpt";
	private final String GRADE = "grade";
	private final String STROKES = "stroke_count";
	
	private ArrayList<String> _characters = new ArrayList<String>();
	private Character _character;
	private boolean _inLiteral = false;
	private boolean _inJlpt = false;
	private boolean _inGrade = false;
	private boolean _inStroke = false;
	
	@Override
	public void startDocument() throws SAXException{
		if(!_characters.isEmpty()){
			// Empty the list of characters
			_characters.clear();
		}
	}
	
	@Override
	public void endDocument() throws SAXException{
		
	}
	
	@Override
	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException{
		_character = new Character();
		if(localName.equals(LITERAL)){
			_inLiteral = true;
		}else if(localName.equals(JLPT)){
			_inJlpt = true;
		}else if(localName.equals(GRADE)){
			_inGrade = true;
		}else if(localName.equals(STROKES)){
			_inStroke = true;
		}
	}
	
	@Override
	public void endElement(String namespaceURI, String localName, String qName) throws SAXException{
		if(localName.equals(LITERAL)){
			_inLiteral = false;
			// Now that we got all the information of the current character, add it to the list
			_characters.add(_character.literal);
		}else if(localName.equals(JLPT)){
			_inJlpt = false;
		}else if(localName.equals(GRADE)){
			_inGrade = false;
		}else if(localName.equals(STROKES)){
			_inStroke = false;
		}
	}
	
	@Override
	public void characters(char ch[], int start, int length){
		String chars = new String(ch, start, length);
		chars = chars.trim();
		if(_inLiteral){
			_character.literal = chars;
		}else if(_inJlpt){
			_character.jlpt = chars;
		}else if(_inGrade){
			_character.grade = chars;
		}else if(_inStroke){
			_character.strokeCount = chars;
		}
	}
	
	public ArrayList<String> getAllCharacters(){
		return _characters;
	}
	
}
