package giraudsa.marshall.serialisation.binary.actions.simple;


import utils.champ.FieldInformations;
import utils.headers.HeaderSimpleType;

import java.io.IOException;

import giraudsa.marshall.exception.MarshallExeption;
import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.binary.ActionBinary;

public class ActionBinaryShort extends ActionBinary<Short>{

	public ActionBinaryShort() {
		super();
	}
	
	@Override
	protected void ecritValeur(Marshaller marshaller, Short objetASerialiser, FieldInformations fieldInformations, boolean isDejaVu) throws IOException {
		//rien a faire
	}
	
	@Override
	protected boolean writeHeaders(Marshaller marshaller, Short s, FieldInformations fieldInformations)
			throws MarshallExeption, IOException {
		HeaderSimpleType<?> header = (HeaderSimpleType<?>) HeaderSimpleType.getHeader(s);
		header.writeValue(getOutput(marshaller), s);
		return false;
	}
}
