package io.github.giraudsa.fidelis.serialisation.binary.actions;

import java.io.IOException;
import java.util.Calendar;

import io.github.giraudsa.fidelis.serialisation.Marshaller;
import io.github.giraudsa.fidelis.serialisation.binary.ActionBinary;
import io.github.giraudsa.fidelis.utils.champ.FieldInformations;

public class ActionBinaryCalendar extends ActionBinary<Calendar> {

	public ActionBinaryCalendar() {
		super();
	}

	@Override
	protected void ecritValeur(final Marshaller marshaller, final Calendar calendar,
			final FieldInformations fieldInformations, final boolean isDejaVu) throws IOException {
		if (!isDejaVu) {
			setDejaTotalementSerialise(marshaller, calendar);
			writeInt(marshaller, calendar.get(Calendar.YEAR));
			writeInt(marshaller, calendar.get(Calendar.MONTH));
			writeInt(marshaller, calendar.get(Calendar.DAY_OF_MONTH));
			writeInt(marshaller, calendar.get(Calendar.HOUR_OF_DAY));
			writeInt(marshaller, calendar.get(Calendar.MINUTE));
			writeInt(marshaller, calendar.get(Calendar.SECOND));
		}
	}
}