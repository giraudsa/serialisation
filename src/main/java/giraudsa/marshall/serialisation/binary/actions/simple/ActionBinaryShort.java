package giraudsa.marshall.serialisation.binary.actions.simple;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.serialisation.binary.BinaryMarshaller;

import java.io.IOException;

public class ActionBinaryShort extends ActionBinarySimple<Short>{

	public ActionBinaryShort(BinaryMarshaller b) {
		super(b);
	}
	
	@Override
	public void ecritValeur(Short objetASerialiser, TypeRelation typeRelation) throws IOException {
		writeShort(objetASerialiser);
	}

}
