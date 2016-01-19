package giraudsa.marshall.serialisation.binary.actions.simple;

import giraudsa.marshall.serialisation.Marshaller;

public class ActionBinaryVoid extends ActionBinarySimple<Void> {

	public ActionBinaryVoid() {
		super();
	}
	
	@Override
	protected byte[] calculHeaders(Marshaller marshaller, Void objetASerialiser, boolean typeDevinable, boolean isDejaVu) {
		return getHeaderConstant(getType(objetASerialiser), typeDevinable);
	}
}
