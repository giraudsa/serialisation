package giraudsa.marshall.deserialisation.binary.actions.simple;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.BinaryUnmarshaller;

public class ActionBinaryCalendar<C extends Calendar> extends ActionBinarySimple<C> {

	public static ActionAbstrait<Calendar> getInstance() {
		return new ActionBinaryCalendar<>(Calendar.class, null);
	}

	private ActionBinaryCalendar(final Class<C> type, final BinaryUnmarshaller<?> unmarshaller) {
		super(type, unmarshaller);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public <U extends C> ActionAbstrait<U> getNewInstance(final Class<U> type, final Unmarshaller unmarshaller) {
		return new ActionBinaryCalendar<>(type, (BinaryUnmarshaller<?>) unmarshaller);
	}

	@Override
	protected void initialise() throws IOException {
		final boolean isDejaVu = isDejaVu();
		if (isDejaVu)
			obj = getObjet();
		else {
			obj = new GregorianCalendar(readInt(), readInt(), readInt(), readInt(), readInt(), readInt());
			// day, month, day of month, hour of day, minute, second
			stockeObjetId();
			setDejaTotalementDeSerialise();
		}
	}
}
