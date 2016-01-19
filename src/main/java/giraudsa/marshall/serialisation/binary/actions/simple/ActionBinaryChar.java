package giraudsa.marshall.serialisation.binary.actions.simple;


import utils.champ.FieldInformations;

import java.io.IOException;

import giraudsa.marshall.serialisation.Marshaller;

public class ActionBinaryChar  extends ActionBinarySimple<Character>{

	public ActionBinaryChar() {
		super();
	}
	
	@Override
	protected void ecritValeur(Marshaller marshaller, Character objetASerialiser, FieldInformations fieldInformations) throws IOException {
		writeChar(marshaller, objetASerialiser);
	}
	
	@Override
	protected byte[] calculHeaders(Marshaller marshaller, Character objetASerialiser, boolean typeDevinable, boolean isDejaVu) {
		return getHeaderConstant(getType(objetASerialiser), typeDevinable);
	}
}
