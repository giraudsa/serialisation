package giraudsa.marshall.deserialisation.text.xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

public class XmlUnmarshallerHandler extends DefaultHandler2 {
	private final XmlUnmarshaller<?> xmlUnmarshaller;

	public XmlUnmarshallerHandler(final XmlUnmarshaller<?> xmlUnmarshaller) {
		super();
		this.xmlUnmarshaller = xmlUnmarshaller;
	}

	@Override
	public void characters(final char[] caracteres, final int debut, final int longueur) throws SAXException {

		final String donnees = new String(caracteres, debut, longueur);
		try {
			xmlUnmarshaller.characters(donnees);
		} catch (final Exception e) {
			throw new SAXException(e);
		}
	}

	@Override
	public void endElement(final String uri, final String localName, final String qName) throws SAXException {
		try {
			xmlUnmarshaller.endElement();
		} catch (final Exception e) {
			throw new SAXException(e);
		}
	}

	@Override
	public void startElement(final String uri, final String localName, final String qName, final Attributes attributes)
			throws SAXException {
		try {
			xmlUnmarshaller.startElement(qName, attributes);
		} catch (final Exception e) {
			throw new SAXException(e);
		}
	}

}
