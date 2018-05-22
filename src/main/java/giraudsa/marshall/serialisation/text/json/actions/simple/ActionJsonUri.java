package giraudsa.marshall.serialisation.text.json.actions.simple;

import java.net.URI;

import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.text.json.actions.ActionJsonSimpleWithQuote;

public class ActionJsonUri extends ActionJsonSimpleWithQuote<URI> {

	public ActionJsonUri() {
		super();
	}

	@Override
	protected String getAEcrire(final Marshaller marshaller, final URI uri) {
		return uri.toASCIIString();
	}
}
