package giraudsa.marshall.serialisation.binary.actions.simple;

import utils.Constants;
import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.serialisation.binary.BinaryMarshaller;



public class ActionBinaryBoolean extends ActionBinarySimple<Boolean> {

	private byte[] headerTrue = new byte[]{Constants.BOOL_VALUE.TRUE};
	private byte[] headerFalse = new byte[]{Constants.BOOL_VALUE.FALSE};
	
	public ActionBinaryBoolean(BinaryMarshaller b) {
		super(b);
	}

	@Override
	protected byte[] calculHeaders(Boolean objetASerialiser, TypeRelation typeRelation, boolean typeDevinable, boolean isDejaVu) {
		return objetASerialiser ? headerTrue : headerFalse;
	}


}
