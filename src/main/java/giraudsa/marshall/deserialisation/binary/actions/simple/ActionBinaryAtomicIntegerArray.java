package giraudsa.marshall.deserialisation.binary.actions.simple;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicIntegerArray;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.BinaryUnmarshaller;

@SuppressWarnings("rawtypes")
public class ActionBinaryAtomicIntegerArray extends ActionBinarySimple<AtomicIntegerArray> {
	public static ActionAbstrait<AtomicIntegerArray> getInstance() { // NOSONAR
		return new ActionBinaryAtomicIntegerArray(AtomicIntegerArray.class, null);
	}

	private ActionBinaryAtomicIntegerArray(final Class<AtomicIntegerArray> type, final BinaryUnmarshaller<?> b) {
		super(type, b);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <U extends AtomicIntegerArray> ActionAbstrait<U> getNewInstance(final Class<U> type,
			final Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionBinaryAtomicIntegerArray(AtomicIntegerArray.class,
				(BinaryUnmarshaller<?>) unmarshaller);
	}

	@Override
	protected void initialise() throws IOException {
		if (isDejaVu())
			obj = getObjet();
		else {
			final int taille = readInt();
			obj = new AtomicIntegerArray(taille);
			for (int i = 0; i < taille; ++i)
				((AtomicIntegerArray) obj).set(i, readInt());
			stockeObjetId();
			setDejaTotalementDeSerialise();
		}
	}
}
