package giraudsa.marshall.deserialisation.binary.actions.simple;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.BinaryUnmarshaller;

public class ActionBinaryAtomicInteger extends ActionBinarySimple<AtomicInteger> {

	public static ActionAbstrait<AtomicInteger> getInstance() {
		return new ActionBinaryAtomicInteger(AtomicInteger.class, null);
	}

	private ActionBinaryAtomicInteger(final Class<AtomicInteger> type, final BinaryUnmarshaller<?> unmarshaller) {
		super(type, unmarshaller);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <U extends AtomicInteger> ActionAbstrait<U> getNewInstance(final Class<U> type,
			final Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionBinaryAtomicInteger(AtomicInteger.class,
				(BinaryUnmarshaller<?>) unmarshaller);
	}

	@Override
	protected void initialise() throws IOException {
		if (isDejaVu())
			obj = getObjet();
		else {
			obj = new AtomicInteger(readInt());
			stockeObjetId();
			setDejaTotalementDeSerialise();
		}
	}
}
