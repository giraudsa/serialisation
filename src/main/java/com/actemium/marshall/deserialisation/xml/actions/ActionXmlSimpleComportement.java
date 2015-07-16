package com.actemium.marshall.deserialisation.xml.actions;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang3.StringEscapeUtils;

import com.actemium.marshall.deserialisation.xml.ActionXml;

public class ActionXmlSimpleComportement<T> extends ActionXml<T> {

	public ActionXmlSimpleComportement(Class<T> type, String nom) {
		super(type, nom);
	}

	@Override
	protected void rempliData(String donnees) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		obj = type.getConstructor(String.class).newInstance(StringEscapeUtils.unescapeXml(donnees));
	}
}
