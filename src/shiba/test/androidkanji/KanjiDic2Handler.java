package shiba.test.androidkanji;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class KanjiDic2Handler extends DefaultHandler {

	private final String LITERAL_ELEMENT = "literal";
	
	private ArrayList<String> _characters = new ArrayList<String>();
	private Character _character;
	private boolean _inLiteral = false;
	
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
		if(localName.equals(LITERAL_ELEMENT)){
			_inLiteral = true;
		}
	}
	
	@Override
	public void endElement(String namespaceURI, String localName, String qName) throws SAXException{
		if(localName.equals(LITERAL_ELEMENT)){
			_inLiteral = false;
			// Now that we got all the information of the current character, add it to the list
			_characters.add(_character.literal);
		}
	}
	
	@Override
	public void characters(char ch[], int start, int length){
		String chars = new String(ch, start, length);
		chars = chars.trim();
		if(_inLiteral){
			_character.literal = chars;
		}
	}
	
	public ArrayList<String> getAllCharacters(){
		return _characters;
	}
	
}
