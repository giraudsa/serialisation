package giraudsa.marshall.serialisation.binary.actions.simple;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.serialisation.binary.BinaryMarshaller;

import java.io.IOException;

public class ActionBinaryShort extends ActionBinarySimple<Short>{

	public ActionBinaryShort(Class<? super Short> type, BinaryMarshaller b) {
		super(type, b);
	}
	
	@Override
	public void serialise(Object objetASerialiser, TypeRelation typeRelation, boolean couldBeLessSpecific) {
		writeHeaders(objetASerialiser, typeRelation, couldBeLessSpecific);
		try {
			writeShort((short)objetASerialiser);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
