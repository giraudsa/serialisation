package giraudsa.marshall.serialisation.binary.actions.simple;

import java.io.IOException;

import giraudsa.marshall.serialisation.binary.BinaryMarshaller;
import utils.champ.FieldInformations;

public class ActionBinaryInteger  extends ActionBinarySimple<Integer>{

	public ActionBinaryInteger(BinaryMarshaller b) {
		super(b);
	}

	@Override
	protected void ecritValeur(Integer objetASerialiser, FieldInformations fieldInformations) throws IOException {
		writeInt(objetASerialiser);
	}

}
