package giraudsa.marshall.serialisation.text.xml.actions;

import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.text.xml.ActionXml;
import utils.champ.FieldInformations;

import java.io.IOException;

public class ActionXmlVoid extends ActionXml<Void> {

	public ActionXmlVoid() {
		super();
	}

	@Override
	protected void ecritValeur(Marshaller marshaller, Void obj, FieldInformations fieldInformations) throws IOException{
		write(marshaller, "null");
	}
	
}
