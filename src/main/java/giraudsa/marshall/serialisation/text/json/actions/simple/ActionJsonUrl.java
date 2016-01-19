package giraudsa.marshall.serialisation.text.json.actions.simple;

import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.text.json.actions.ActionJsonSimpleWithQuote;
import java.net.URL;

public class ActionJsonUrl extends ActionJsonSimpleWithQuote<URL> {

	public ActionJsonUrl() {
		super();
	}

	@Override
	protected String getAEcrire(Marshaller marshaller, URL uri) {
		return uri.toExternalForm();
	}
}
