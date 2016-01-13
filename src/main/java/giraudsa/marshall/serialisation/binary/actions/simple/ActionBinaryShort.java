package giraudsa.marshall.serialisation.binary.actions.simple;

import giraudsa.marshall.serialisation.binary.BinaryMarshaller;
import utils.champ.FieldInformations;

import java.io.IOException;

public class ActionBinaryShort extends ActionBinarySimple<Short>{

	public ActionBinaryShort(BinaryMarshaller b) {
		super(b);
	}
	
	@Override
	protected void ecritValeur(Short objetASerialiser, FieldInformations fieldInformations) throws IOException {
		writeShort(objetASerialiser);
	}

}
