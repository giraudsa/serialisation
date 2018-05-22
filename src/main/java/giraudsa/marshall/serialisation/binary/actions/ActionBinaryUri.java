package giraudsa.marshall.serialisation.binary.actions;

import java.io.IOException;
import java.net.URI;

import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.binary.ActionBinary;
import utils.champ.FieldInformations;

public class ActionBinaryUri extends ActionBinary<URI> {

	public ActionBinaryUri() {
		super();
	}

	@Override
	protected void ecritValeur(final Marshaller marshaller, final URI uri, final FieldInformations fieldInformations,
			final boolean isDejaVu) throws IOException {
		if (!isDejaVu) {
			setDejaTotalementSerialise(marshaller, uri);
			writeUTF(marshaller, uri.toASCIIString());
		}
	}
}