package giraudsa.marshall.serialisation.binary.actions.simple;

import java.io.IOException;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.serialisation.binary.BinaryMarshaller;

public class ActionBinaryFloat  extends ActionBinarySimple<Float> {

	public ActionBinaryFloat(BinaryMarshaller b) {
		super(b);
	}
	
	@Override
	public void ecritValeur(Float objetASerialiser, TypeRelation typeRelation) throws IOException {
		writeFloat(objetASerialiser);
	}
}
