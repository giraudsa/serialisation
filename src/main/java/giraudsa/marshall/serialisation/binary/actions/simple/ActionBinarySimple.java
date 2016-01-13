package giraudsa.marshall.serialisation.binary.actions.simple;

import java.io.IOException;

import giraudsa.marshall.serialisation.binary.ActionBinary;
import giraudsa.marshall.serialisation.binary.BinaryMarshaller;
import utils.champ.FieldInformations;

public class ActionBinarySimple<T> extends ActionBinary<T> {

	public ActionBinarySimple(BinaryMarshaller b) {
		super(b);
	}
	
	@Override
	protected byte[] calculHeaders(T objetASerialiser, boolean typeDevinable, boolean isDejaVu) {
		return getHeaderConstant(getType(objetASerialiser), typeDevinable);
	}
	
	@Override
	protected <U> boolean isDejaVu(U objet) {
		return false;
	}

	@Override
	protected void ecritValeur(T objetASerialiser, FieldInformations fieldInformations) throws IOException {
		//rien a faire
	}

}
