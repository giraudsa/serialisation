package giraudsa.marshall.serialisation.binary.actions.simple;

import java.io.IOException;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.serialisation.binary.BinaryMarshaller;

public class ActionBinaryInteger  extends ActionBinarySimple<Integer>{

	public ActionBinaryInteger(Class<? super Integer> type, BinaryMarshaller b) {
		super(type, b);
	}

	@Override
	public void serialise(Object objetASerialiser, TypeRelation typeRelation, boolean couldBeLessSpecific) {
		writeHeaders(objetASerialiser, typeRelation, couldBeLessSpecific);
		try {
			writeInt((int)objetASerialiser);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
