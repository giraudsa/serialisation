package giraudsa.marshall.serialisation.text.json.actions.simple;

import java.net.URL;

import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.text.json.actions.ActionJsonSimpleWithQuote;

public class ActionJsonUrl extends ActionJsonSimpleWithQuote<URL> {

	public ActionJsonUrl() {
		super();
	}

	@Override
	protected String getAEcrire(final Marshaller marshaller, final URL uri) {
		return uri.toExternalForm();
	}
}
