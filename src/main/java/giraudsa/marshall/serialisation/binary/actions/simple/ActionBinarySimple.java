package giraudsa.marshall.serialisation.binary.actions.simple;

import java.io.IOException;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.serialisation.binary.ActionBinary;
import giraudsa.marshall.serialisation.binary.BinaryMarshaller;

public class ActionBinarySimple<T> extends ActionBinary<T> {

	public ActionBinarySimple(BinaryMarshaller b) {
		super(b);
	}
	
	@Override
	protected byte[] calculHeaders(T objetASerialiser, TypeRelation typeRelation, boolean typeDevinable, boolean isDejaVu) {
		return getHeaderConstant(getType(objetASerialiser), typeDevinable);
	}
	
	@Override
	protected <U> boolean isDejaVu(U objet) {
		return false;
	}

	@Override
	public void ecritValeur(T objetASerialiser, TypeRelation typeRelation) throws IOException {
	}

}
