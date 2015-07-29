package giraudsa.marshall.serialisation.binary.actions.simple;

import utils.Constants;
import giraudsa.marshall.serialisation.binary.BinaryMarshaller;

public class ActionBinaryVoid extends ActionBinarySimple<Boolean> {

	public ActionBinaryVoid(Class<? super Boolean> type, BinaryMarshaller b) {
		super(type, b);
		this.headerConstant = new byte[]{Constants.IS_NULL};
	}
}
