package giraudsa.marshall.serialisation.binary.actions.simple;

import java.io.IOException;

import giraudsa.marshall.serialisation.binary.BinaryMarshaller;
import utils.champ.FieldInformations;

public class ActionBinaryDouble  extends ActionBinarySimple<Double> {

	public ActionBinaryDouble(BinaryMarshaller b) {
		super(b);
	}

	@Override
	protected void ecritValeur(Double objetASerialiser, FieldInformations fieldInformations) throws IOException {
		writeDouble(objetASerialiser);
	}
}
