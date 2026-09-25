package io.github.giraudsa.fidelis.serialisation.binary.actions;

import java.io.IOException;
import java.net.URL;

import io.github.giraudsa.fidelis.serialisation.Marshaller;
import io.github.giraudsa.fidelis.serialisation.binary.ActionBinary;
import io.github.giraudsa.fidelis.utils.champ.FieldInformations;

public class ActionBinaryUrl extends ActionBinary<URL> {

	public ActionBinaryUrl() {
		super();
	}

	@Override
	protected void ecritValeur(final Marshaller marshaller, final URL url, final FieldInformations fieldInformations,
			final boolean isDejaVu) throws IOException {
		if (!isDejaVu) {
			setDejaTotalementSerialise(marshaller, url);
			writeUTF(marshaller, url.toExternalForm());
		}
	}
}