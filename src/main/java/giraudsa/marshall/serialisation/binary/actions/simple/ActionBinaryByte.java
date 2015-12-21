package giraudsa.marshall.serialisation.binary.actions.simple;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.serialisation.binary.BinaryMarshaller;

import java.io.IOException;

public class ActionBinaryByte extends ActionBinarySimple<Byte> {

	
	public ActionBinaryByte(BinaryMarshaller b) {
		super(b);
	}
	
	@Override
	public void ecritValeur(Byte objetASerialiser, TypeRelation typeRelation) throws IOException{
		writeByte(objetASerialiser);
	}
}
