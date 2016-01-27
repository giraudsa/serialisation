package giraudsa.marshall.serialisation.binary.actions;

import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.binary.ActionBinary;

import utils.champ.FieldInformations;

import java.io.IOException;
import java.net.URL;

public class ActionBinaryUrl extends ActionBinary<URL> {

	public ActionBinaryUrl() {
		super();
	}
	@Override
	protected void ecritValeur(Marshaller marshaller, URL url, FieldInformations fieldInformations, boolean isDejaVu) throws IOException {
		if(!isDejaVu){
			setDejaTotalementSerialise(marshaller, url);
			writeUTF(marshaller, url.toExternalForm());
		}
	}
}