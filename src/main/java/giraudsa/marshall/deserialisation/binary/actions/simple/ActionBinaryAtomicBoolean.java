package giraudsa.marshall.deserialisation.binary.actions.simple;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.BinaryUnmarshaller;

public class ActionBinaryAtomicBoolean extends ActionBinarySimple<AtomicBoolean> {

	public static ActionAbstrait<AtomicBoolean> getInstance() {
		return new ActionBinaryAtomicBoolean(AtomicBoolean.class, null);
	}

	private ActionBinaryAtomicBoolean(final Class<AtomicBoolean> type, final BinaryUnmarshaller<?> unmarshaller) {
		super(type, unmarshaller);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <U extends AtomicBoolean> ActionAbstrait<U> getNewInstance(final Class<U> type,
			final Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionBinaryAtomicBoolean(AtomicBoolean.class,
				(BinaryUnmarshaller<?>) unmarshaller);
	}

	@Override
	protected void initialise() throws IOException {
		if (isDejaVu())
			obj = getObjet();
		else {
			obj = new AtomicBoolean(readBoolean());
			stockeObjetId();
			setDejaTotalementDeSerialise();
		}
	}
}
