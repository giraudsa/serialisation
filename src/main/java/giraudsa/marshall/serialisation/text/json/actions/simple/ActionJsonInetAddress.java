package giraudsa.marshall.serialisation.text.json.actions.simple;

import java.net.InetAddress;

import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.text.json.actions.ActionJsonSimpleWithQuote;

public class ActionJsonInetAddress extends ActionJsonSimpleWithQuote<InetAddress> {

	public ActionJsonInetAddress() {
		super();
	}

	@Override
	protected String getAEcrire(final Marshaller marshaller, final InetAddress address) {
		return address.getHostAddress();
	}
}
