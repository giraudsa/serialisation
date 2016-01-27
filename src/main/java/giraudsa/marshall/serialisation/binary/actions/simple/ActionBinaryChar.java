package giraudsa.marshall.serialisation.binary.actions.simple;


import utils.champ.FieldInformations;
import utils.headers.HeaderSimpleType;

import java.io.IOException;

import giraudsa.marshall.exception.MarshallExeption;
import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.binary.ActionBinary;

public class ActionBinaryChar  extends ActionBinary<Character>{

	public ActionBinaryChar() {
		super();
	}
	
	@Override
	protected boolean writeHeaders(Marshaller marshaller, Character caractere, FieldInformations fieldInformations)
			throws IOException, MarshallExeption {
		HeaderSimpleType<?> header = (HeaderSimpleType<?>) HeaderSimpleType.getHeader(caractere);
		header.writeValue(getOutput(marshaller), caractere);
		return false;
	}
	
	@Override
	protected void ecritValeur(Marshaller marshaller, Character objetASerialiser, FieldInformations fieldInformations, boolean isDejaVu) throws IOException {
		//rien a faire
	}
	
}
