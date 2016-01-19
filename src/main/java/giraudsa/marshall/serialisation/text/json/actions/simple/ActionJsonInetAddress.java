package giraudsa.marshall.serialisation.text.json.actions.simple;

import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.text.json.actions.ActionJsonSimpleWithQuote;

import java.net.InetAddress;

public class ActionJsonInetAddress extends ActionJsonSimpleWithQuote<InetAddress> {

	public ActionJsonInetAddress() {
		super();
	}

	@Override
	protected String getAEcrire(Marshaller marshaller, InetAddress address) {
		return address.getHostAddress();
	}
}
