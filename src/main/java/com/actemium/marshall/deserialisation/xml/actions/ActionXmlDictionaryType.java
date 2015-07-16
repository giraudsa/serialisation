package com.actemium.marshall.deserialisation.xml.actions;

import java.util.Map;

import com.actemium.marshall.deserialisation.xml.ActionXml;

@SuppressWarnings("rawtypes")
public class ActionXmlDictionaryType<T extends Map> extends ActionXml<T> {
	private Object keyTampon;
	
	public ActionXmlDictionaryType(Class<T> type, String nom) throws InstantiationException, IllegalAccessException {
		super(type, nom);
		obj = type.newInstance();
	}

	@SuppressWarnings("unchecked") @Override
	protected <W> void integreObjet(String nomAttribut, W objet) {
		if(keyTampon == null) keyTampon = objet;
		obj.put(keyTampon, objet);
		keyTampon = null;
	}

}
