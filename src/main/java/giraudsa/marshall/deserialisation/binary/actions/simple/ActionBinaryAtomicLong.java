package giraudsa.marshall.deserialisation.binary.actions.simple;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.BinaryUnmarshaller;

public class ActionBinaryAtomicLong extends ActionBinarySimple<AtomicLong> {

	public static ActionAbstrait<AtomicLong> getInstance() {
		return new ActionBinaryAtomicLong(AtomicLong.class, null);
	}

	private ActionBinaryAtomicLong(final Class<AtomicLong> type, final BinaryUnmarshaller<?> unmarshaller) {
		super(type, unmarshaller);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <U extends AtomicLong> ActionAbstrait<U> getNewInstance(final Class<U> type,
			final Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionBinaryAtomicLong(AtomicLong.class, (BinaryUnmarshaller<?>) unmarshaller);
	}

	@Override
	protected void initialise() throws IOException {
		if (isDejaVu())
			obj = getObjet();
		else {
			obj = new AtomicLong(readLong());
			stockeObjetId();
			setDejaTotalementDeSerialise();
		}
	}
}
