package giraudsa.marshall.deserialisation.text.xml.actions;

import giraudsa.marshall.deserialisation.text.xml.ActionXml;
import giraudsa.marshall.deserialisation.text.xml.XmlUnmarshaller;

import java.text.ParseException;
import java.util.Date;

public class ActionXmlDate extends ActionXml<Date>{
	public ActionXmlDate(Class<Date> type, String nom, XmlUnmarshaller<?> xmlUnmarshaller) {
		super(type, nom, xmlUnmarshaller);
	}
	
	@Override
	protected void rempliData(String donnees) throws ParseException {
		obj = getDateFormat().parse(donnees);
	}
}
