package giraudsa.marshall.serialisation.binary.actions.simple;

import java.io.IOException;

import giraudsa.marshall.serialisation.binary.BinaryMarshaller;
import utils.champ.FieldInformations;

public class ActionBinaryFloat  extends ActionBinarySimple<Float> {

	public ActionBinaryFloat(BinaryMarshaller b) {
		super(b);
	}
	
	@Override
	protected void ecritValeur(Float objetASerialiser, FieldInformations fieldInformations) throws IOException {
		writeFloat(objetASerialiser);
	}
}
