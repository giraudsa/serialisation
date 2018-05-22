package giraudsa.marshall.deserialisation.binary.actions.simple;

import java.io.IOException;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.BinaryUnmarshaller;

public class ActionBinaryStringBuffer extends ActionBinarySimple<StringBuffer> {

	public static ActionAbstrait<StringBuffer> getInstance() {
		return new ActionBinaryStringBuffer(StringBuffer.class, null);
	}

	private ActionBinaryStringBuffer(final Class<StringBuffer> type, final BinaryUnmarshaller<?> unmarshaller) {
		super(type, unmarshaller);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <U extends StringBuffer> ActionAbstrait<U> getNewInstance(final Class<U> type,
			final Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionBinaryStringBuffer(StringBuffer.class,
				(BinaryUnmarshaller<?>) unmarshaller);
	}

	@Override
	protected void initialise() throws IOException {
		if (isDejaVu())
			obj = getObjet();
		else {
			obj = new StringBuffer(readUTF());
			stockeObjetId();
			setDejaTotalementDeSerialise();
		}
	}
}
