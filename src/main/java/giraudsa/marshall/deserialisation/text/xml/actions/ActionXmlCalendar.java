package giraudsa.marshall.deserialisation.text.xml.actions;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.xml.XmlUnmarshaller;

import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import utils.champ.FakeChamp;
import utils.champ.FieldInformations;


public class ActionXmlCalendar<Calendar> extends ActionXmlComplexeObject<Calendar> {
	private static FakeChamp cSeconde = new FakeChamp("second", Integer.class, TypeRelation.COMPOSITION);
	private static FakeChamp cMinute = new FakeChamp("minute", Integer.class, TypeRelation.COMPOSITION);
	private static FakeChamp cHourOfDay = new FakeChamp("hourOfDay", Integer.class, TypeRelation.COMPOSITION);
	private static FakeChamp cDayOfMonth = new FakeChamp("dayOfMonth", Integer.class, TypeRelation.COMPOSITION);
	private static FakeChamp cMonth = new FakeChamp("month", Integer.class, TypeRelation.COMPOSITION);
	private static FakeChamp cYear = new FakeChamp("year", Integer.class, TypeRelation.COMPOSITION);
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
	
	private ActionXmlCalendar(Class<Calendar> type, XmlUnmarshaller<?> xmlUnmarshaller) {
		super(type, xmlUnmarshaller);
		map = new HashMap<>();
	}

	public static  ActionAbstrait<Object> getInstance() {	
		return new ActionXmlCalendar<>(Object.class, null);
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public <U extends Calendar> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return new ActionXmlCalendar<>(type, (XmlUnmarshaller<?>)unmarshaller);
	}
	
	@Override
	protected  void construitObjet(){
		obj = new GregorianCalendar(map.get(cYear), map.get(cMonth), map.get(cDayOfMonth), map.get(cHourOfDay), map.get(cMinute), map.get(cSeconde));
	}
	
	@Override
	protected <W> void integreObjet(String nomAttribut, W objet){
		FakeChamp champ = nomToChamp.get(nomAttribut);
		map.put(champ, (Integer) objet);
	}
	
    @Override
	protected Class<?> getTypeAttribute(String nomAttribut) {
		return Integer.class;
	}
    
    @Override 
    protected FieldInformations getFieldInformationSpecialise(String nomAttribut){
		return nomToChamp.get(nomAttribut);
	}
}
