package com.actemium.marshall.deserialisation.xml.actions;

import java.util.Collection;

import com.actemium.marshall.deserialisation.xml.ActionXml;

@SuppressWarnings("rawtypes")
public class ActionXmlCollectionType<T extends Collection> extends ActionXml<T> {
    
	public ActionXmlCollectionType(Class<T> type, String nom) throws InstantiationException, IllegalAccessException {
		super(type, nom);
		obj = type.newInstance();
	}

	
	@SuppressWarnings("unchecked")	@Override
	protected <W> void integreObjet(String nomAttribut, W objet) {
		obj.add(objet);
	}

}
