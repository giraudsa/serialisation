package giraudsa.marshall.serialisation.text.xml.actions;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.serialisation.text.xml.ActionXml;
import giraudsa.marshall.serialisation.text.xml.XmlMarshaller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


public class ActionXmlDate<DateGeneric> extends ActionXml<DateGeneric>{
	
	@Override
	protected Class<?> getType() {
		return Date.class;
	}
	private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	private static TimeZone  tz = TimeZone.getTimeZone("UTC");
	static{
		df.setTimeZone(tz);
	}
	
	public ActionXmlDate(Class<DateGeneric> type, XmlMarshaller xmlM, String nomBalise){
		super(type, xmlM, nomBalise);
		if(nomBalise == null) balise = "Date";
	}
	

	@Override
	protected void ecritValeur(DateGeneric date, TypeRelation relation) throws IOException  {
		write(df.format(date));
	}
}
