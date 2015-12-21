package giraudsa.marshall.serialisation.binary.actions;

import java.io.IOException;
import java.util.UUID;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.serialisation.binary.ActionBinary;
import giraudsa.marshall.serialisation.binary.BinaryMarshaller;

public class ActionBinaryUUID extends ActionBinary<UUID> {

	public ActionBinaryUUID(BinaryMarshaller b) {
		super(b);
	}
	@Override
	protected void ecritValeur(UUID id, TypeRelation typeRelation) throws IOException {
		if(!isDejaTotalementSerialise(id)){
			setDejaTotalementSerialise(id);
			writeUTF(id.toString());
		}
	}

}
