package giraudsa.marshall.serialisation.binary.actions.simple;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.serialisation.binary.ActionBinary;
import giraudsa.marshall.serialisation.binary.BinaryMarshaller;

public class ActionBinarySimple<T> extends ActionBinary<T> {

	public ActionBinarySimple(Class<? super T> type, BinaryMarshaller b) {
		super(type, b);
	}
	
	@Override
	protected byte[] calculHeaders(Object objetASerialiser, TypeRelation typeRelation, boolean couldBeLessSpecific, boolean isDejaVu) {
		return headerConstant;
	}
	
	@Override
	protected <U> boolean isDejaVu(U objet) {
		return false;
	}

	@Override
	public void serialise(Object objetASerialiser, TypeRelation typeRelation, boolean couldBeLessSpecific) {
		boolean isDejaVu = writeHeaders(objetASerialiser, typeRelation, couldBeLessSpecific);
	}

}
