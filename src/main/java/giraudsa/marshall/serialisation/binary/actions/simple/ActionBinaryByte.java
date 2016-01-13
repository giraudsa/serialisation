package giraudsa.marshall.serialisation.binary.actions.simple;

import giraudsa.marshall.serialisation.binary.BinaryMarshaller;
import utils.champ.FieldInformations;

import java.io.IOException;

public class ActionBinaryByte extends ActionBinarySimple<Byte> {

	
	public ActionBinaryByte(BinaryMarshaller b) {
		super(b);
	}
	
	@Override
	protected void ecritValeur(Byte objetASerialiser, FieldInformations fieldInformations) throws IOException{
		writeByte(objetASerialiser);
	}
}
