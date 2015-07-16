package com.actemium.marshall.deserialisation.json;

import com.actemium.marshall.deserialisation.ActionAbstrait;

public abstract class ActionJson<T> extends ActionAbstrait<T> {

	public ActionJson(Class<T> type, String nom) {
		super(type, nom);
	}

	protected abstract Class<?> getType(String clefEnCours);

}
