package giraudsa.marshall.serialisation.binary.actions;

import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.binary.ActionBinary;

import utils.champ.FieldInformations;

import java.io.IOException;
import java.util.Calendar;

public class ActionBinaryCalendar extends ActionBinary<Calendar> {

	public ActionBinaryCalendar() {
		super();
	}
	@Override
	protected void ecritValeur(Marshaller marshaller, Calendar calendar, FieldInformations fieldInformations, boolean isDejaVu) throws IOException {
		if(!isDejaVu){
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