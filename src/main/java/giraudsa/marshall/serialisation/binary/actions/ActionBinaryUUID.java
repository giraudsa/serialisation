package giraudsa.marshall.serialisation.binary.actions;

import java.io.IOException;
import java.util.UUID;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.serialisation.binary.ActionBinary;
import giraudsa.marshall.serialisation.binary.BinaryMarshaller;

public class ActionBinaryUUID extends ActionBinary<UUID> {

	public ActionBinaryUUID(Class<? super UUID> type, BinaryMarshaller b) {
		super(type, b);
	}
	@Override
	protected void serialise(Object objetASerialiser, TypeRelation typeRelation, boolean couldBeLessSpecific) {
		boolean isDejaVu = writeHeaders(objetASerialiser, typeRelation, couldBeLessSpecific);
		try {
			if(!isDejaVu)
				writeUTF(objetASerialiser.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
