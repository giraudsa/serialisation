package giraudsa.marshall.serialisation.binary.actions.simple;


import utils.champ.FieldInformations;
import utils.headers.HeaderSimpleType;

import java.io.IOException;

import giraudsa.marshall.exception.MarshallExeption;
import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.binary.ActionBinary;

public class ActionBinaryByte extends ActionBinary<Byte> {

	
	public ActionBinaryByte() {
		super();
	}
	
	@Override
	protected void ecritValeur(Marshaller marshaller, Byte objetASerialiser, FieldInformations fieldInformations, boolean isDejaVu) throws IOException{
		//rien a faire
	}
	
	@Override
	protected boolean writeHeaders(Marshaller marshaller, Byte octet, FieldInformations fi) throws IOException, MarshallExeption {
		if (!fi.getValueType().isPrimitive()){
			HeaderSimpleType<?> header = (HeaderSimpleType<?>) HeaderSimpleType.getHeader(octet);
			header.writeValue(getOutput(marshaller), octet);
		}else{
			writeByte(marshaller, octet);
		}
		return false;
	}
}
