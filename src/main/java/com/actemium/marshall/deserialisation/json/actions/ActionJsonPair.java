package com.actemium.marshall.deserialisation.json.actions;

import utils.champ.ChampUid;

import com.actemium.marshall.deserialisation.json.ActionJson;
import com.actemium.marshall.serialisation.json.Pair;

public class ActionJsonPair extends ActionJson<Pair> {
	private boolean keyAllreadyHere = false;
	private Object key;
	public ActionJsonPair(Class<Pair> type, String nom) {
		super(type, nom);
	}
	
	@Override
	protected <W> void integreObjet(String nomAttribut, W objet) {
		if(nomAttribut.equals(ChampUid.uidFieldName)) return;
		if(!keyAllreadyHere){
			key = objet;
			keyAllreadyHere = true;
		}
		else{
			obj = new Pair(key, objet);
		}
	}

	@Override
	protected Class<?> getType(String clefEnCours) {
		return null;
	}

}
