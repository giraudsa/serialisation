package giraudsa.marshall.serialisation.text.xml.actions;

import java.io.IOException;
import java.net.URI;

import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.text.xml.ActionXml;
import utils.champ.FieldInformations;

public class ActionXmlUri extends ActionXml<URI> {

	public ActionXmlUri() {
		super();
	}

	@Override
	protected void ecritValeur(final Marshaller marshaller, final URI obj, final FieldInformations fieldInformations,
			final boolean serialiseTout) throws IOException {
		writeEscape(marshaller, obj.toASCIIString());
	}
}
