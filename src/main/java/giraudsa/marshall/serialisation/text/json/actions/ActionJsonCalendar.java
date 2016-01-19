package giraudsa.marshall.serialisation.text.json.actions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.text.json.ActionJson;
import utils.champ.FakeChamp;
import utils.champ.FieldInformations;

public class ActionJsonCalendar extends ActionJson<Calendar> {
	private static FakeChamp fakeChampSeconde = new FakeChamp("second", String.class, TypeRelation.COMPOSITION);
	private static FakeChamp fakeChampMinute = new FakeChamp("minute", String.class, TypeRelation.COMPOSITION);
	private static FakeChamp fakeChampHourOfDay = new FakeChamp("hourOfDay", String.class, TypeRelation.COMPOSITION);
	private static FakeChamp fakeChampDayOfMonth = new FakeChamp("dayOfMonth", String.class, TypeRelation.COMPOSITION);
	private static FakeChamp fakeChampMonth = new FakeChamp("month", String.class, TypeRelation.COMPOSITION);
	private static FakeChamp fakeChampYear = new FakeChamp("year", String.class, TypeRelation.COMPOSITION);


	public ActionJsonCalendar() {
		super();
	}


	@Override protected void ecritValeur(Marshaller marshaller, Calendar calendar, FieldInformations fieldInformations, boolean ecrisSeparateur) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException, IOException{
		pushComportement(marshaller, traiteChamp(marshaller, calendar.get(Calendar.SECOND), fakeChampSeconde, true));
		pushComportement(marshaller, traiteChamp(marshaller, calendar.get(Calendar.MINUTE), fakeChampMinute, true));
		pushComportement(marshaller, traiteChamp(marshaller, calendar.get(Calendar.HOUR_OF_DAY), fakeChampHourOfDay, true));
		pushComportement(marshaller, traiteChamp(marshaller, calendar.get(Calendar.DAY_OF_MONTH), fakeChampDayOfMonth, true));
		pushComportement(marshaller, traiteChamp(marshaller, calendar.get(Calendar.MONTH), fakeChampMonth, true));
		pushComportement(marshaller, traiteChamp(marshaller, calendar.get(Calendar.YEAR), fakeChampYear, ecrisSeparateur));
	}
	
	@Override protected boolean commenceObject(Marshaller marshaller, Calendar calendar, boolean typeDevinable) throws IOException {
		ouvreAccolade(marshaller);
		if(!typeDevinable) {
			ecritType(marshaller, calendar);
			return true;
		}
		return false;
	}
	
	@Override protected void clotureObject(Marshaller marshaller, Calendar calendar, boolean typeDevinable) throws IOException {
		fermeAccolade(marshaller);
	}


}
