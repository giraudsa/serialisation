package io.github.giraudsa.fidelis.deserialisation.binary.actions.simple;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;

import io.github.giraudsa.fidelis.deserialisation.ActionAbstrait;
import io.github.giraudsa.fidelis.deserialisation.Unmarshaller;
import io.github.giraudsa.fidelis.deserialisation.binary.BinaryUnmarshaller;
import io.github.giraudsa.fidelis.exception.UnmarshallExeption;

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
