package giraudsa.marshall.serialisation.binary.actions.simple;

import java.io.IOException;

import giraudsa.marshall.serialisation.Marshaller;
import utils.champ.FieldInformations;

public class ActionBinaryFloat  extends ActionBinarySimple<Float> {

	public ActionBinaryFloat() {
		super();
	}
	
	@Override
	protected void ecritValeur(Marshaller marshaller, Float objetASerialiser, FieldInformations fieldInformations) throws IOException {
		writeFloat(marshaller, objetASerialiser);
	}
	
	@Override
	protected byte[] calculHeaders(Marshaller marshaller, Float objetASerialiser, boolean typeDevinable, boolean isDejaVu) {
		return getHeaderConstant(getType(objetASerialiser), typeDevinable);
	}
}
