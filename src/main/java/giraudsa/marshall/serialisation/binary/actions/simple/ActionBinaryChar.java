package giraudsa.marshall.serialisation.binary.actions.simple;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.serialisation.binary.BinaryMarshaller;

import java.io.IOException;

public class ActionBinaryChar  extends ActionBinarySimple<Character>{

	public ActionBinaryChar(BinaryMarshaller b) {
		super(b);
	}
	
	@Override
	public void ecritValeur(Character objetASerialiser, TypeRelation typeRelation) throws IOException {
		writeChar(objetASerialiser);
	}
}
