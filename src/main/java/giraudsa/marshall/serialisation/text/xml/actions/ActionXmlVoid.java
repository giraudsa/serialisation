package giraudsa.marshall.serialisation.text.xml.actions;

import giraudsa.marshall.serialisation.text.xml.ActionXml;
import giraudsa.marshall.serialisation.text.xml.XmlMarshaller;
import utils.champ.FieldInformations;

import java.io.IOException;

public class ActionXmlVoid extends ActionXml<Void> {

	public ActionXmlVoid(XmlMarshaller xmlM) {
		super(xmlM);
	}

	@Override
	protected void ecritValeur(Void obj, FieldInformations fieldInformations) throws IOException{
		write("null");
	}
	
}
