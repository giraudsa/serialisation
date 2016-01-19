package giraudsa.marshall.serialisation.binary.actions.simple;

import java.io.IOException;

import giraudsa.marshall.serialisation.Marshaller;
import utils.champ.FieldInformations;

public class ActionBinaryDouble  extends ActionBinarySimple<Double> {

	public ActionBinaryDouble() {
		super();
	}

	@Override
	protected void ecritValeur(Marshaller marshaller, Double objetASerialiser, FieldInformations fieldInformations) throws IOException {
		writeDouble(marshaller, objetASerialiser);
	}
	
	@Override
	protected byte[] calculHeaders(Marshaller marshaller, Double objetASerialiser, boolean typeDevinable, boolean isDejaVu) {
		return getHeaderConstant(getType(objetASerialiser), typeDevinable);
	}
}
