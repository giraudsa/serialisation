package giraudsa.marshall.deserialisation.binary.actions.simple;

import java.io.IOException;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.BinaryUnmarshaller;

public class ActionBinaryStringBuilder extends ActionBinarySimple<StringBuilder> {

	public static ActionAbstrait<StringBuilder> getInstance() {
		return new ActionBinaryStringBuilder(StringBuilder.class, null);
	}

	private ActionBinaryStringBuilder(final Class<StringBuilder> type, final BinaryUnmarshaller<?> unmarshaller) {
		super(type, unmarshaller);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <U extends StringBuilder> ActionAbstrait<U> getNewInstance(final Class<U> type,
			final Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionBinaryStringBuilder(StringBuilder.class,
				(BinaryUnmarshaller<?>) unmarshaller);
	}

	@Override
	protected void initialise() throws IOException {
		if (isDejaVu())
			obj = getObjet();
		else {
			obj = new StringBuilder(readUTF());
			stockeObjetId();
			setDejaTotalementDeSerialise();
		}
	}
}
