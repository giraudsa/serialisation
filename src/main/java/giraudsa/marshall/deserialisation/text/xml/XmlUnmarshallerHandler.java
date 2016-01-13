package giraudsa.marshall.deserialisation.text.xml;



import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;


public class XmlUnmarshallerHandler extends DefaultHandler2 {
	private XmlUnmarshaller<?> xmlUnmarshaller;
	
	public XmlUnmarshallerHandler(XmlUnmarshaller<?> xmlUnmarshaller) {
		super();
		this.xmlUnmarshaller = xmlUnmarshaller;
	}

	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		try {
			xmlUnmarshaller.startElement(qName, attributes);
		} catch (Exception e) {
			throw new SAXException(e);			
		}
	}	


	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		try {
			xmlUnmarshaller.endElement();
		} catch (Exception e) {
			throw new SAXException(e);			
		}
	}

	

	@Override
	public void characters(char[] caracteres, int debut, int longueur) throws SAXException{
		
		String donnees = new String(caracteres, debut, longueur);
		try {
			xmlUnmarshaller.characters(donnees);
		} catch (Exception e) {
			throw new SAXException(e);			
		}
	}

	 
}
