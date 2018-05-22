package giraudsa.marshall.serialisation.text.xml.actions;

import java.io.IOException;
import java.net.URL;

import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.text.xml.ActionXml;
import utils.champ.FieldInformations;

public class ActionXmlUrl extends ActionXml<URL> {

	public ActionXmlUrl() {
		super();
	}

	@Override
	protected void ecritValeur(final Marshaller marshaller, final URL obj, final FieldInformations fieldInformations,
			final boolean serialiseTout) throws IOException {
		writeEscape(marshaller, obj.toExternalForm());
	}
}
