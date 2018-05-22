package giraudsa.marshall.serialisation.binary.actions;

import java.io.IOException;
import java.net.URL;

import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.binary.ActionBinary;
import utils.champ.FieldInformations;

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