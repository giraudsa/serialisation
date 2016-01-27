package giraudsa.marshall.serialisation.binary.actions.simple;


import utils.champ.FieldInformations;
import utils.headers.HeaderSimpleType;

import java.io.IOException;

import giraudsa.marshall.exception.MarshallExeption;
import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.binary.ActionBinary;

public class ActionBinaryLong  extends ActionBinary<Long>{

	public ActionBinaryLong() {
		super();
	}
	
	@Override
	protected void ecritValeur(Marshaller marshaller, Long objetASerialiser, FieldInformations fieldInformations, boolean isDejaVu) throws IOException {
		//rien a faire
	}
	
	@Override
	protected boolean writeHeaders(Marshaller marshaller, Long l, FieldInformations fieldInformations)
			throws MarshallExeption, IOException {
		HeaderSimpleType<?> header = (HeaderSimpleType<?>) HeaderSimpleType.getHeader(l);
		header.writeValue(getOutput(marshaller), l);
		return false;
	}

}
