package com.actemium.marshall.deserialisation.json.actions;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.actemium.marshall.deserialisation.json.ActionJson;

public class ActionJsonDate extends ActionJson<Date>{
	
	private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	private static TimeZone tz = TimeZone.getTimeZone("UTC");
	static{
		df.setTimeZone(tz);
	}
	
	public ActionJsonDate(Class<Date> type, String nom) {
		super(type, nom);
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
