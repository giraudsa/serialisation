package giraudsa.marshall.serialisation.binary.actions.simple;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.serialisation.binary.BinaryMarshaller;

import java.io.IOException;

public class ActionBinaryLong  extends ActionBinarySimple<Long>{

	public ActionBinaryLong(BinaryMarshaller b) {
		super(b);
	}
	
	@Override
	public void ecritValeur(Long objetASerialiser, TypeRelation typeRelation) throws IOException {
		writeLong(objetASerialiser);
	}

}
