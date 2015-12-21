package giraudsa.marshall.serialisation.binary.actions.simple;

import java.io.IOException;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.serialisation.binary.BinaryMarshaller;

public class ActionBinaryDouble  extends ActionBinarySimple<Double> {

	public ActionBinaryDouble(BinaryMarshaller b) {
		super(b);
	}

	@Override
	public void ecritValeur(Double objetASerialiser, TypeRelation typeRelation) throws IOException {
		writeDouble(objetASerialiser);
	}
}
