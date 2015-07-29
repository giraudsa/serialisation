package giraudsa.marshall.serialisation.binary.actions.simple;

import java.io.IOException;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.serialisation.binary.BinaryMarshaller;

public class ActionBinaryDouble  extends ActionBinarySimple<Double> {

	public ActionBinaryDouble(Class<? super Double> type, BinaryMarshaller b) {
		super(type, b);
	}

	@Override
	public void serialise(Object objetASerialiser, TypeRelation typeRelation, boolean couldBeLessSpecific) {
		writeHeaders(objetASerialiser, typeRelation, couldBeLessSpecific);
		try {
			writeDouble((double)objetASerialiser);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
