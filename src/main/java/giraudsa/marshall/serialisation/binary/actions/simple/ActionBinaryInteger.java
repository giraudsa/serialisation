package giraudsa.marshall.serialisation.binary.actions.simple;

import java.io.IOException;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.serialisation.binary.BinaryMarshaller;

public class ActionBinaryInteger  extends ActionBinarySimple<Integer>{

	public ActionBinaryInteger(BinaryMarshaller b) {
		super(b);
	}

	@Override
	public void ecritValeur(Integer objetASerialiser, TypeRelation typeRelation) throws IOException {
		writeInt(objetASerialiser);
	}

}
