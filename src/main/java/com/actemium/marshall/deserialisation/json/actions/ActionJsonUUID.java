package com.actemium.marshall.deserialisation.json.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

import com.actemium.marshall.deserialisation.json.ActionJson;

public class ActionJsonUUID extends ActionJson<UUID> {

	public ActionJsonUUID(Class<UUID> type, String nom) {
		super(type, nom);
	}

	@Override
	protected Class<?> getType(String clefEnCours) {
		return type;
	}
	
	@Override
	protected <W> void integreObjet(String nomAttribut, W objet) {
		if(objet instanceof String) obj = UUID.fromString((String) objet);
		else if (objet instanceof UUID) obj = (UUID)objet;
	}
	
	@Override
	protected void rempliData(String donnees) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		obj = UUID.fromString(donnees);
	}
}
