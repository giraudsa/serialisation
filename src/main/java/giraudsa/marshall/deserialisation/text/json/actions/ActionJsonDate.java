package giraudsa.marshall.deserialisation.text.json.actions;

import giraudsa.marshall.deserialisation.text.json.ActionJson;
import giraudsa.marshall.deserialisation.text.json.JsonUnmarshaller;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class ActionJsonDate extends ActionJson<Date>{
	
	private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	private static TimeZone tz = TimeZone.getTimeZone("UTC");
	static{
		df.setTimeZone(tz);
	}
	
	public ActionJsonDate(Class<Date> type, String nom, JsonUnmarshaller<?> jsonUnmarshaller) {
		super(type, nom, jsonUnmarshaller);
	}
	
	@Override
	protected void rempliData(String donnees) throws ParseException {
		obj = df.parse(donnees);
	}

	@Override
	protected Class<?> getType(String clefEnCours) {
		return Date.class;
	}
	
	@Override
	protected <W> void integreObjet(String nomAttribut, W objet) {
		obj = (Date)objet;
	}
}
