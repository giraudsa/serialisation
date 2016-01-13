package giraudsa.marshall.serialisation.text.xml.actions;

import giraudsa.marshall.serialisation.text.xml.ActionXml;
import giraudsa.marshall.serialisation.text.xml.XmlMarshaller;
import utils.champ.FieldInformations;

import java.io.IOException;
import java.util.Date;


public class ActionXmlDate extends ActionXml<Date>{
	
	public ActionXmlDate(XmlMarshaller xmlM){
		super(xmlM);
	}
	

	@Override
	protected void ecritValeur(Date date, FieldInformations f) throws IOException  {
		write(getDateFormat().format(date));
	}
}
