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
	protected void ecritValeur(Marshaller marshaller, URL url, FieldInformations fieldInformations) throws IOException {
		if(!isDejaTotalementSerialise(marshaller, url)){
			setDejaTotalementSerialise(marshaller, url);
			writeUTF(marshaller, url.toExternalForm());
		}
	}
}