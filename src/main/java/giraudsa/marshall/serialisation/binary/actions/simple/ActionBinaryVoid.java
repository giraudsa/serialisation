package giraudsa.marshall.serialisation.binary.actions.simple;

import java.io.IOException;

import giraudsa.marshall.exception.MarshallExeption;
import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.binary.ActionBinary;
import utils.champ.FieldInformations;
import utils.headers.HeaderSimpleType;

public class ActionBinaryVoid extends ActionBinary<Void> {

	public ActionBinaryVoid() {
		super();
	}
	
	@Override
	protected boolean writeHeaders(Marshaller marshaller, Void objetASerialiser, FieldInformations fieldInformations)
			throws MarshallExeption, IOException {
		HeaderSimpleType<?> header = (HeaderSimpleType<?>) HeaderSimpleType.getHeader(null);
		header.writeValue(getOutput(marshaller), null);
		return false;
	}
	@Override
	protected void ecritValeur(Marshaller marshaller, Void objetASerialiser, FieldInformations fieldInformations, boolean isDejaVu) throws IOException {
		//rien à écrire si l'objet est null.
	}
}
