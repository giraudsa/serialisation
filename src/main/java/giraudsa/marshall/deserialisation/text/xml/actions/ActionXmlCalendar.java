package giraudsa.marshall.deserialisation.text.xml.actions;

import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.xml.XmlUnmarshaller;
import utils.champ.FakeChamp;
import utils.champ.FieldInformations;

public class ActionXmlCalendar<Calendar> extends ActionXmlComplexeObject<Calendar> {
	private static FakeChamp cDayOfMonth = new FakeChamp("dayOfMonth", Integer.class, TypeRelation.COMPOSITION, null);
	private static FakeChamp cHourOfDay = new FakeChamp("hourOfDay", Integer.class, TypeRelation.COMPOSITION, null);
	private static FakeChamp cMinute = new FakeChamp("minute", Integer.class, TypeRelation.COMPOSITION, null);
	private static FakeChamp cMonth = new FakeChamp("month", Integer.class, TypeRelation.COMPOSITION, null);
	private static FakeChamp cSeconde = new FakeChamp("second", Integer.class, TypeRelation.COMPOSITION, null);
	private static FakeChamp cYear = new FakeChamp("year", Integer.class, TypeRelation.COMPOSITION, null);
	private static Map<String, FakeChamp> nomToChamp = new HashMap<>();
	static {
		nomToChamp.put(cSeconde.getName(), cSeconde);
		nomToChamp.put(cMinute.getName(), cMinute);
		nomToChamp.put(cHourOfDay.getName(), cHourOfDay);
		nomToChamp.put(cDayOfMonth.getName(), cDayOfMonth);
		nomToChamp.put(cMonth.getName(), cMonth);
		nomToChamp.put(cYear.getName(), cYear);
	}

	public static ActionAbstrait<Object> getInstance() {
		return new ActionXmlCalendar<>(Object.class, null);
	}

	private final Map<FakeChamp, Integer> map;

	private ActionXmlCalendar(final Class<Calendar> type, final XmlUnmarshaller<?> xmlUnmarshaller) {
		super(type, xmlUnmarshaller);
		map = new HashMap<>();
	}

	@Override
	protected void construitObjet() {
		obj = new GregorianCalendar(map.get(cYear), map.get(cMonth), map.get(cDayOfMonth), map.get(cHourOfDay),
				map.get(cMinute), map.get(cSeconde));
	}

	@Override
	protected FieldInformations getFieldInformationSpecialise(final String nomAttribut) {
		return nomToChamp.get(nomAttribut);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public <U extends Calendar> ActionAbstrait<U> getNewInstance(final Class<U> type, final Unmarshaller unmarshaller) {
		return new ActionXmlCalendar<>(type, (XmlUnmarshaller<?>) unmarshaller);
	}

	@Override
	protected Class<?> getTypeAttribute(final String nomAttribut) {
		return Integer.class;
	}

	@Override
	protected <W> void integreObjet(final String nomAttribut, final W objet) {
		final FakeChamp champ = nomToChamp.get(nomAttribut);
		map.put(champ, (Integer) objet);
	}
}
