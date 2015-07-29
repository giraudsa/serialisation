package giraudsa.marshall.serialisation.binary.actions.simple;

import java.io.IOException;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.serialisation.binary.BinaryMarshaller;

public class ActionBinaryFloat  extends ActionBinarySimple<Float> {

	public ActionBinaryFloat(Class<? super Float> type, BinaryMarshaller b) {
		super(type, b);
	}
	
	@Override
	public void serialise(Object objetASerialiser, TypeRelation typeRelation, boolean couldBeLessSpecific) {
		writeHeaders(objetASerialiser, typeRelation, couldBeLessSpecific);
		try {
			writeFloat((float)objetASerialiser);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
