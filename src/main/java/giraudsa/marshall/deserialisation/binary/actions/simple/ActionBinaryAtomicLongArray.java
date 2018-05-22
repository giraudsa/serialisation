package giraudsa.marshall.deserialisation.binary.actions.simple;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLongArray;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.BinaryUnmarshaller;

@SuppressWarnings("rawtypes")
public class ActionBinaryAtomicLongArray extends ActionBinarySimple<AtomicLongArray> {
	public static ActionAbstrait<AtomicLongArray> getInstance() { // NOSONAR
		return new ActionBinaryAtomicLongArray(AtomicLongArray.class, null);
	}

	private ActionBinaryAtomicLongArray(final Class<AtomicLongArray> type, final BinaryUnmarshaller<?> b) {
		super(type, b);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <U extends AtomicLongArray> ActionAbstrait<U> getNewInstance(final Class<U> type,
			final Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionBinaryAtomicLongArray(AtomicLongArray.class,
				(BinaryUnmarshaller<?>) unmarshaller);
	}

	@Override
	protected void initialise() throws IOException {
		if (isDejaVu())
			obj = getObjet();
		else {
			final int taille = readInt();
			obj = new AtomicLongArray(taille);
			for (int i = 0; i < taille; ++i)
				((AtomicLongArray) obj).set(i, readLong());
			stockeObjetId();
			setDejaTotalementDeSerialise();
		}
	}
}
