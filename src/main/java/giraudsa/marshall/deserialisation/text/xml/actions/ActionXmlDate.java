package giraudsa.marshall.deserialisation.text.xml.actions;

import giraudsa.marshall.deserialisation.text.xml.ActionXml;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class ActionXmlDate extends ActionXml<Date>{
	
	private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	private static TimeZone tz = TimeZone.getTimeZone("UTC");
	static{
		df.setTimeZone(tz);
	}
	
	public ActionXmlDate(Class<Date> type, String nom) {
		super(type, nom);
	}
	
	@Override
	protected void rempliData(String donnees) throws ParseException {
		obj = df.parse(donnees);
	}
}
