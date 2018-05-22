package giraudsa.marshall.deserialisation.binary.actions.simple;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.BinaryUnmarshaller;
import giraudsa.marshall.exception.UnmarshallExeption;

public class ActionBinaryDate<D extends Date> extends ActionBinarySimple<D> {

	public static ActionAbstrait<Date> getInstance() {
		return new ActionBinaryDate<>(Date.class, null);
	}

	private ActionBinaryDate(final Class<D> type, final BinaryUnmarshaller<?> unmarshaller) {
		super(type, unmarshaller);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public <U extends D> ActionAbstrait<U> getNewInstance(final Class<U> type, final Unmarshaller unmarshaller) {
		return new ActionBinaryDate<>(type, (BinaryUnmarshaller<?>) unmarshaller);
	}

	@Override
	protected void initialise() throws IOException, UnmarshallExeption {
		final boolean isDejaVu = isDejaVu();
		if (isDejaVu)
			obj = getObjet();
		else {
			try {
				obj = type.getConstructor(long.class).newInstance(readLong());
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				throw new UnmarshallExeption("impossible de construire la date de type " + type.getName(), e);
			}
			stockeObjetId();
			setDejaTotalementDeSerialise();
		}
	}
}
