package giraudsa.marshall.serialisation.text.json.actions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.MarshallExeption;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.text.json.ActionJson;
import utils.champ.FakeChamp;
import utils.champ.FieldInformations;

public class ActionJsonCalendar extends ActionJson<Calendar> {
	private static FakeChamp fakeChampDayOfMonth = new FakeChamp("dayOfMonth", Integer.class, TypeRelation.COMPOSITION,
			null);
	private static FakeChamp fakeChampHourOfDay = new FakeChamp("hourOfDay", Integer.class, TypeRelation.COMPOSITION,
			null);
	private static FakeChamp fakeChampMinute = new FakeChamp("minute", Integer.class, TypeRelation.COMPOSITION, null);
	private static FakeChamp fakeChampMonth = new FakeChamp("month", Integer.class, TypeRelation.COMPOSITION, null);
	private static FakeChamp fakeChampSeconde = new FakeChamp("second", Integer.class, TypeRelation.COMPOSITION, null);
	private static FakeChamp fakeChampYear = new FakeChamp("year", Integer.class, TypeRelation.COMPOSITION, null);

	public ActionJsonCalendar() {
		super();
	}

	@Override
	protected void clotureObject(final Marshaller marshaller, final Calendar calendar, final boolean typeDevinable)
			throws IOException {
		fermeAccolade(marshaller);
	}

	@Override
	protected boolean commenceObject(final Marshaller marshaller, final Calendar calendar, final boolean typeDevinable)
			throws IOException {
		ouvreAccolade(marshaller);
		if (!typeDevinable) {
			ecritType(marshaller, calendar);
			return true;
		}
		return false;
	}

	@Override
	protected void ecritValeur(final Marshaller marshaller, final Calendar calendar,
			final FieldInformations fieldInformations, final boolean ecrisSeparateur)
			throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException,
			NotImplementedSerializeException, IOException, MarshallExeption {
		pushComportement(marshaller, traiteChamp(marshaller, calendar.get(Calendar.SECOND), fakeChampSeconde, true));
		pushComportement(marshaller, traiteChamp(marshaller, calendar.get(Calendar.MINUTE), fakeChampMinute, true));
		pushComportement(marshaller,
				traiteChamp(marshaller, calendar.get(Calendar.HOUR_OF_DAY), fakeChampHourOfDay, true));
		pushComportement(marshaller,
				traiteChamp(marshaller, calendar.get(Calendar.DAY_OF_MONTH), fakeChampDayOfMonth, true));
		pushComportement(marshaller, traiteChamp(marshaller, calendar.get(Calendar.MONTH), fakeChampMonth, true));
		pushComportement(marshaller,
				traiteChamp(marshaller, calendar.get(Calendar.YEAR), fakeChampYear, ecrisSeparateur));
	}

}
