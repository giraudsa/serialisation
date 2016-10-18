package giraudsa.marshall.deserialisation.text.json.actions;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.json.ActionJson;
import giraudsa.marshall.deserialisation.text.json.JsonUnmarshaller;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import utils.TypeExtension;
import utils.champ.Champ;
import utils.champ.FakeChamp;
import utils.champ.FieldInformations;

public class ActionJsonCalendar<C extends Calendar> extends ActionJson<Calendar> {
	private static FakeChamp cSeconde = new FakeChamp("second", Integer.class, TypeRelation.COMPOSITION, null);
	private static FakeChamp cMinute = new FakeChamp("minute", Integer.class, TypeRelation.COMPOSITION, null);
	private static FakeChamp cHourOfDay = new FakeChamp("hourOfDay", Integer.class, TypeRelation.COMPOSITION, null);
	private static FakeChamp cDayOfMonth = new FakeChamp("dayOfMonth", Integer.class, TypeRelation.COMPOSITION, null);
	private static FakeChamp cMonth = new FakeChamp("month", Integer.class, TypeRelation.COMPOSITION, null);
	private static FakeChamp cYear = new FakeChamp("year", Integer.class, TypeRelation.COMPOSITION, null);
	private static Map<String, FakeChamp> nomToChamp = new HashMap<>();
	static{
		nomToChamp.put(cSeconde.getName(), cSeconde);
		nomToChamp.put(cMinute.getName(), cMinute);
		nomToChamp.put(cHourOfDay.getName(), cHourOfDay);
		nomToChamp.put(cDayOfMonth.getName(), cDayOfMonth);
		nomToChamp.put(cMonth.getName(), cMonth);
		nomToChamp.put(cYear.getName(), cYear);
	}
	private Map<FakeChamp, Integer> map;
	
	private ActionJsonCalendar(Class<Calendar> type, JsonUnmarshaller<?> jsonUnmarshaller) {
		super(type, jsonUnmarshaller);
		map = new HashMap<>();
	}

	public static ActionAbstrait<?> getInstance(){
		return new ActionJsonCalendar<>(Calendar.class, null);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <U extends Calendar> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionJsonCalendar(type, (JsonUnmarshaller<?>)unmarshaller);
	}

	
	@Override protected Class<?> getTypeAttribute(String nomAttribut) {
		FieldInformations champ = TypeExtension.getChampByName(type, nomAttribut);
		if (champ.isSimple())
			return TypeExtension.getTypeEnveloppe(champ.getValueType());//on renvoie Integer à la place de int, Double au lieu de double, etc...
		return champ.getValueType();
	}
	
	@Override
	protected <W> void integreObjet(String nomAttribut, W objet){
		FakeChamp champ = nomToChamp.get(nomAttribut);
		map.put(champ, (Integer) objet);
	}
	
	@Override
	protected void construitObjet() {
		obj = new GregorianCalendar(map.get(cYear), map.get(cMonth), map.get(cDayOfMonth), map.get(cHourOfDay), map.get(cMinute), map.get(cSeconde));
	}
	
	@Override 
    protected FieldInformations getFieldInformationSpecialise(String nomAttribut){
		return nomToChamp.get(nomAttribut);
	}

	@Override
	protected void rempliData(String donnees) {
		// rien a faire ici
		
	}



}
