package com.actemium.marshall.deserialisation.xml.actions;

import java.lang.reflect.InvocationTargetException;

import com.actemium.marshall.deserialisation.Unmarshaller;

public class ActionXmlString extends ActionXmlSimpleComportement<String>{

	StringBuilder sb = new StringBuilder();
	
	public ActionXmlString(Class<String> type, String nom) {
		super(type, nom);
	}
	
	@Override protected void rempliData(String donnees) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		sb.append(donnees);
	}
	
	@Override protected <U> void construitObjet(Unmarshaller<U> unmarshaller) throws InstantiationException, IllegalAccessException {
		obj = sb.toString();
	}

}
