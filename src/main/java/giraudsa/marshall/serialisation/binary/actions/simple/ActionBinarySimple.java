package giraudsa.marshall.serialisation.binary.actions.simple;

import java.io.IOException;

import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.binary.ActionBinary;

import utils.champ.FieldInformations;

public class ActionBinarySimple<T> extends ActionBinary<T> {

	public ActionBinarySimple() {
		super();
	}
	
	@Override
	protected <U> boolean isDejaVu(Marshaller marshaller, U objet) {
		return false;
	}

	@Override
	protected void ecritValeur(Marshaller marshaller, T objetASerialiser, FieldInformations fieldInformations) throws IOException {
		writeUTF(marshaller, objetASerialiser.toString());
	}

}
