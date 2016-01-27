package giraudsa.marshall.serialisation.binary.actions.simple;

import java.io.IOException;

import giraudsa.marshall.exception.MarshallExeption;
import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.binary.ActionBinary;
import utils.champ.FieldInformations;
import utils.headers.HeaderSimpleType;

public class ActionBinaryFloat  extends ActionBinary<Float> {

	public ActionBinaryFloat() {
		super();
	}
	
	@Override
	protected void ecritValeur(Marshaller marshaller, Float objetASerialiser, FieldInformations fieldInformations, boolean isDejaVu) throws IOException {
		//rien a faire
	}
	
	@Override
	protected boolean writeHeaders(Marshaller marshaller, Float f, FieldInformations fieldInformations)
			throws IOException, MarshallExeption {
		HeaderSimpleType<?> header = (HeaderSimpleType<?>) HeaderSimpleType.getHeader(f);
		header.writeValue(getOutput(marshaller), f);
		return false;
	}
}
