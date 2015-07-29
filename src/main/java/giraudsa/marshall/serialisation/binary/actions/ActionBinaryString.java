package giraudsa.marshall.serialisation.binary.actions;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.serialisation.binary.ActionBinary;
import giraudsa.marshall.serialisation.binary.BinaryMarshaller;

import java.io.IOException;

public class ActionBinaryString extends ActionBinary<String> {

	public ActionBinaryString(Class<? super String> type, BinaryMarshaller b) {
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