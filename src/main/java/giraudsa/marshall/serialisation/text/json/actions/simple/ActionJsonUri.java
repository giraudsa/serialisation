package giraudsa.marshall.serialisation.text.json.actions.simple;

import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.text.json.actions.ActionJsonSimpleWithQuote;
import java.net.URI;

public class ActionJsonUri extends ActionJsonSimpleWithQuote<URI> {

	public ActionJsonUri() {
		super();
	}

	@Override
	protected String getAEcrire(Marshaller marshaller, URI uri) {
		return uri.toASCIIString();
	}
}
