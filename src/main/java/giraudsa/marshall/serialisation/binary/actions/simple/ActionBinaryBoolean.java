package giraudsa.marshall.serialisation.binary.actions.simple;

import utils.Constants;
import giraudsa.marshall.serialisation.binary.BinaryMarshaller;



public class ActionBinaryBoolean extends ActionBinarySimple<Boolean> {

	private byte[] headerTrue = new byte[]{Constants.BoolValue.TRUE};
	private byte[] headerFalse = new byte[]{Constants.BoolValue.FALSE};
	
	public ActionBinaryBoolean(BinaryMarshaller b) {
		super(b);
	}

	@Override
	protected byte[] calculHeaders(Boolean objetASerialiser, boolean typeDevinable, boolean isDejaVu) {
		return objetASerialiser ? headerTrue : headerFalse;
	}


}
