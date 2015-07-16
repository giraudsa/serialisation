package com.actemium.marshall.deserialisation.xml;

import com.actemium.marshall.deserialisation.ActionAbstrait;

public abstract class ActionXml<T> extends ActionAbstrait<T> {
	
	public ActionXml(Class<T> type, String nom) {
		super(type, nom);
	}

}
