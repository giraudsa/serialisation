package giraudsa.marshall.serialisation.binary.actions;

import java.io.IOException;
import java.net.InetAddress;

import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.binary.ActionBinary;
import utils.champ.FieldInformations;

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