package io.github.giraudsa.fidelis.serialisation.text.json.actions.simple;

import java.net.InetAddress;

import io.github.giraudsa.fidelis.serialisation.Marshaller;
import io.github.giraudsa.fidelis.serialisation.text.json.actions.ActionJsonSimpleWithQuote;

public class ActionJsonInetAddress extends ActionJsonSimpleWithQuote<InetAddress> {

	public ActionJsonInetAddress() {
		super();
	}

	@Override
	protected String getAEcrire(final Marshaller marshaller, final InetAddress address) {
		return address.getHostAddress();
	}
}
