package giraudsa.marshall.serialisation.text.xml.actions;

import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.text.xml.ActionXml;
import utils.champ.FieldInformations;

import java.io.IOException;
import java.net.URL;

public class ActionXmlUrl extends ActionXml<URL> {

	public ActionXmlUrl() {
		super();
	}

	@Override
	protected void ecritValeur(Marshaller marshaller, URL obj, FieldInformations fieldInformations, boolean serialiseTout) throws IOException{
		writeEscape(marshaller, obj.toExternalForm());
	}
}
