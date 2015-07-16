package com.actemium.marshall.serialisation.xml.actions;

import java.io.IOException;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.actemium.marshall.annotations.TypeRelation;
import com.actemium.marshall.serialisation.Marshaller;
import com.actemium.marshall.serialisation.Marshaller.SetQueue;
import com.actemium.marshall.serialisation.xml.ActionXml;


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
	
	public ActionXmlDate(Class<DateGeneric> type, String nomBalise){
		super(type, nomBalise);
		if(nomBalise == null) balise = "Date";
	}
	

	@Override
	protected void ecritValeur(DateGeneric date, TypeRelation relation, Marshaller xmlM, SetQueue<Object> aSerialiser) throws IOException  {
		write(xmlM, df.format(date));
	}
}
