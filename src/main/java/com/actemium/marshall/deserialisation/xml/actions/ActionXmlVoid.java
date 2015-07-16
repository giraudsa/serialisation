package com.actemium.marshall.deserialisation.xml.actions;

public class ActionXmlVoid<T> extends ActionXmlSimpleComportement<T> {

	public ActionXmlVoid(Class<T> type, String nom) {
		super(type, nom);
	}
	
	@Override
	protected T getObjet() {
		return null;
	}
}
