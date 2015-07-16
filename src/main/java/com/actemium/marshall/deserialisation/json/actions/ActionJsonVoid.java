package com.actemium.marshall.deserialisation.json.actions;

public class ActionJsonVoid<T> extends ActionJsonSimpleComportement<T> {

	public ActionJsonVoid(Class<T> type, String nom) {
		super(type, nom);
	}
	
	@Override
	protected T getObjet() {
		return null;
	}
}
