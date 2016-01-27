package giraudsa.marshall.serialisation.binary.actions.simple;

import java.io.IOException;

import giraudsa.marshall.exception.MarshallExeption;
import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.binary.ActionBinary;
import utils.champ.FieldInformations;
import utils.headers.HeaderSimpleType;

public class ActionBinaryInteger  extends ActionBinary<Integer>{

	public ActionBinaryInteger() {
		super();
	}

	@Override
	protected void ecritValeur(Marshaller marshaller, Integer objetASerialiser, FieldInformations fieldInformations, boolean isDejaVu) throws IOException {
		//rien a faire
	}

	@Override
	protected boolean writeHeaders(Marshaller marshaller, Integer i, FieldInformations fieldInformations)
			throws IOException, MarshallExeption {
		HeaderSimpleType<?> header = (HeaderSimpleType<?>) HeaderSimpleType.getHeader(i);
		header.writeValue(getOutput(marshaller), i);
		return false;
	}
}
