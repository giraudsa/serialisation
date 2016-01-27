package giraudsa.marshall.serialisation.binary.actions;

import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.binary.ActionBinary;

import utils.champ.FieldInformations;

import java.io.IOException;
import java.net.URI;

public class ActionBinaryUri extends ActionBinary<URI> {

	public ActionBinaryUri() {
		super();
	}
	@Override
	protected void ecritValeur(Marshaller marshaller, URI uri, FieldInformations fieldInformations, boolean isDejaVu) throws IOException {
		if(!isDejaVu){
			setDejaTotalementSerialise(marshaller, uri);
			writeUTF(marshaller, uri.toASCIIString());
		}
	}
}