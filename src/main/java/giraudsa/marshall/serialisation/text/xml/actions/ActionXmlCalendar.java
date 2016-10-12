package giraudsa.marshall.serialisation.text.xml.actions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.MarshallExeption;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.text.xml.ActionXml;
import utils.champ.FakeChamp;
import utils.champ.FieldInformations;

public class ActionXmlCalendar extends ActionXml<Calendar> {
	private static FakeChamp fakeChampSeconde = new FakeChamp("second", Integer.class, TypeRelation.COMPOSITION, null);
	private static FakeChamp fakeChampMinute = new FakeChamp("minute", Integer.class, TypeRelation.COMPOSITION, null);
	private static FakeChamp fakeChampHourOfDay = new FakeChamp("hourOfDay", Integer.class, TypeRelation.COMPOSITION, null);
	private static FakeChamp fakeChampDayOfMonth = new FakeChamp("dayOfMonth", Integer.class, TypeRelation.COMPOSITION, null);
	private static FakeChamp fakeChampMonth = new FakeChamp("month", Integer.class, TypeRelation.COMPOSITION, null);
	private static FakeChamp fakeChampYear = new FakeChamp("year", Integer.class, TypeRelation.COMPOSITION, null);


	public ActionXmlCalendar() {
		super();
	}


	@Override protected void ecritValeur(Marshaller marshaller, Calendar calendar, FieldInformations fieldInformations) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException, IOException, MarshallExeption{
		pushComportement(marshaller,traiteChamp(marshaller,calendar.get(Calendar.SECOND), fakeChampSeconde));
		pushComportement(marshaller,traiteChamp(marshaller,calendar.get(Calendar.MINUTE), fakeChampMinute));
		pushComportement(marshaller,traiteChamp(marshaller,calendar.get(Calendar.HOUR_OF_DAY), fakeChampHourOfDay));
		pushComportement(marshaller,traiteChamp(marshaller,calendar.get(Calendar.DAY_OF_MONTH), fakeChampDayOfMonth));
		pushComportement(marshaller,traiteChamp(marshaller,calendar.get(Calendar.MONTH), fakeChampMonth));
		pushComportement(marshaller,traiteChamp(marshaller,calendar.get(Calendar.YEAR), fakeChampYear));
	}

}
