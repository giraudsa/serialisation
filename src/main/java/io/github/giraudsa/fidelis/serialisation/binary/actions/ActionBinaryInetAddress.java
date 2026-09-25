package io.github.giraudsa.fidelis.serialisation.binary.actions;

import java.io.IOException;
import java.net.InetAddress;

import io.github.giraudsa.fidelis.serialisation.Marshaller;
import io.github.giraudsa.fidelis.serialisation.binary.ActionBinary;
import io.github.giraudsa.fidelis.utils.champ.FieldInformations;

public class ActionBinaryInetAddress extends ActionBinary<InetAddress> {

	public ActionBinaryInetAddress() {
		super();
	}

	@Override
	protected void ecritValeur(final Marshaller marshaller, final InetAddress address,
			final FieldInformations fieldInformations, final boolean isDejaVu) throws IOException {
		if (!isDejaVu) {
			setDejaTotalementSerialise(marshaller, address);
			writeUTF(marshaller, address.getHostAddress());
		}
	}
}