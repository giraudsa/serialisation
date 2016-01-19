package giraudsa.marshall.serialisation.binary.actions;

import java.io.IOException;
import java.util.UUID;

import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.binary.ActionBinary;

import utils.champ.FieldInformations;

public class ActionBinaryUUID extends ActionBinary<UUID> {

	public ActionBinaryUUID() {
		super();
	}
	@Override
	protected void ecritValeur(Marshaller marshaller, UUID id, FieldInformations fieldInformations) throws IOException{
		if(!isDejaTotalementSerialise(marshaller, id)){
			setDejaTotalementSerialise(marshaller, id);
			writeUTF(marshaller, id.toString());
		}
	}

}
