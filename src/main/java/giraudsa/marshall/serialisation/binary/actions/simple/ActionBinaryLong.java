package giraudsa.marshall.serialisation.binary.actions.simple;


import utils.champ.FieldInformations;

import java.io.IOException;

import giraudsa.marshall.serialisation.Marshaller;

public class ActionBinaryLong  extends ActionBinarySimple<Long>{

	public ActionBinaryLong() {
		super();
	}
	
	@Override
	protected void ecritValeur(Marshaller marshaller, Long objetASerialiser, FieldInformations fieldInformations) throws IOException {
		writeLong(marshaller, objetASerialiser);
	}
	
	@Override
	protected byte[] calculHeaders(Marshaller marshaller, Long objetASerialiser, boolean typeDevinable, boolean isDejaVu) {
		return getHeaderConstant(getType(objetASerialiser), typeDevinable);
	}

}
