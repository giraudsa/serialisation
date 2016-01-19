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
	protected void ecritValeur(Marshaller marshaller, Calendar calendar, FieldInformations fieldInformations) throws IOException {
		if(!isDejaTotalementSerialise(marshaller, calendar)){
			setDejaTotalementSerialise(marshaller, calendar);
			writeLong(marshaller, calendar.getTime().getTime());
		}
	}
}