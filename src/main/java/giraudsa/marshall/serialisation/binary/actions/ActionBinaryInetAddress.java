package giraudsa.marshall.serialisation.binary.actions;

import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.binary.ActionBinary;

import utils.champ.FieldInformations;

import java.io.IOException;
import java.net.InetAddress;

public class ActionBinaryInetAddress extends ActionBinary<InetAddress> {

	public ActionBinaryInetAddress() {
		super();
	}
	@Override
	protected void ecritValeur(Marshaller marshaller, InetAddress address, FieldInformations fieldInformations, boolean isDejaVu) throws IOException {
		if(!isDejaVu){
			setDejaTotalementSerialise(marshaller, address);
			writeUTF(marshaller, address.getHostAddress());
		}
	}
}