package giraudsa.marshall.serialisation.binary.actions.simple;

import giraudsa.marshall.serialisation.binary.BinaryMarshaller;
import utils.champ.FieldInformations;

import java.io.IOException;

public class ActionBinaryChar  extends ActionBinarySimple<Character>{

	public ActionBinaryChar(BinaryMarshaller b) {
		super(b);
	}
	
	@Override
	protected void ecritValeur(Character objetASerialiser, FieldInformations fieldInformations) throws IOException {
		writeChar(objetASerialiser);
	}
}
