package giraudsa.marshall.serialisation.binary.actions.simple;

import utils.Constants;
import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.serialisation.binary.BinaryMarshaller;



public class ActionBinaryBoolean extends ActionBinarySimple<Boolean> {

	private byte[] headerTrue = new byte[]{Constants.BOOL_VALUE.TRUE};
	private byte[] headerFalse = new byte[]{Constants.BOOL_VALUE.FALSE};
	
	public ActionBinaryBoolean(Class<? super Boolean> type, BinaryMarshaller b) {
		super(type, b);
	}

	@Override
	protected byte[] calculHeaders(Object objetASerialiser, TypeRelation typeRelation, boolean couldBeLessSpecific, boolean isDejaVu) {
		return (Boolean)objetASerialiser ? headerTrue : headerFalse;
	}


}
