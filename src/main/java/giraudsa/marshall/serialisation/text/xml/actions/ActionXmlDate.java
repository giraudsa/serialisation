package giraudsa.marshall.serialisation.text.xml.actions;

import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.text.xml.ActionXml;
import utils.champ.FieldInformations;

import java.io.IOException;
import java.util.Date;


public class ActionXmlDate extends ActionXml<Date>{
	
	public ActionXmlDate(){
		super();
	}
	

	@Override
	protected void ecritValeur(Marshaller marshaller, Date date, FieldInformations f, boolean serialiseTout) throws IOException  {
		write(marshaller, getDateFormat(marshaller).format(date));
	}
}
