package io.github.giraudsa.fidelis.serialisation.binary.actions;

import java.io.IOException;
import java.net.URI;

import io.github.giraudsa.fidelis.serialisation.Marshaller;
import io.github.giraudsa.fidelis.serialisation.binary.ActionBinary;
import io.github.giraudsa.fidelis.utils.champ.FieldInformations;

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