package giraudsa.marshall.serialisation.binary.actions.simple;

import java.io.IOException;

import giraudsa.marshall.serialisation.Marshaller;
import utils.champ.FieldInformations;

public class ActionBinaryInteger  extends ActionBinarySimple<Integer>{

	public ActionBinaryInteger() {
		super();
	}

	@Override
	protected void ecritValeur(Marshaller marshaller, Integer objetASerialiser, FieldInformations fieldInformations) throws IOException {
		writeInt(marshaller, objetASerialiser);
	}
	
	@Override
	protected byte[] calculHeaders(Marshaller marshaller, Integer objetASerialiser, boolean typeDevinable, boolean isDejaVu) {
		return getHeaderConstant(getType(objetASerialiser), typeDevinable);
	}

}
