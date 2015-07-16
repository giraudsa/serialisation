package com.actemium.marshall.deserialisation.xml.actions;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.actemium.marshall.deserialisation.xml.ActionXml;

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
