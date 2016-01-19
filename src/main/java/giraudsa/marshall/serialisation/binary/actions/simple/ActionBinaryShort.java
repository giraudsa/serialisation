package giraudsa.marshall.serialisation.binary.actions.simple;


import utils.champ.FieldInformations;

import java.io.IOException;

import giraudsa.marshall.serialisation.Marshaller;

public class ActionBinaryShort extends ActionBinarySimple<Short>{

	public ActionBinaryShort() {
		super();
	}
	
	@Override
	protected void ecritValeur(Marshaller marshaller, Short objetASerialiser, FieldInformations fieldInformations) throws IOException {
		writeShort(marshaller, objetASerialiser);
	}
	
	@Override
	protected byte[] calculHeaders(Marshaller marshaller, Short objetASerialiser, boolean typeDevinable, boolean isDejaVu) {
		return getHeaderConstant(getType(objetASerialiser), typeDevinable);
	}

}
