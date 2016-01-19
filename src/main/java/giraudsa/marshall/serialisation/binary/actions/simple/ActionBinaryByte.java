package giraudsa.marshall.serialisation.binary.actions.simple;


import utils.champ.FieldInformations;

import java.io.IOException;

import giraudsa.marshall.serialisation.Marshaller;

public class ActionBinaryByte extends ActionBinarySimple<Byte> {

	
	public ActionBinaryByte() {
		super();
	}
	
	@Override
	protected void ecritValeur(Marshaller marshaller, Byte objetASerialiser, FieldInformations fieldInformations) throws IOException{
		writeByte(marshaller, objetASerialiser);
	}
	
	@Override
	protected byte[] calculHeaders(Marshaller marshaller, Byte objetASerialiser, boolean typeDevinable, boolean isDejaVu) {
		return getHeaderConstant(getType(objetASerialiser), typeDevinable);
	}
}
