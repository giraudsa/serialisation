package com.actemium.marshall.deserialisation.json.actions;

import java.util.ArrayList;
import java.util.Map;

import com.actemium.marshall.deserialisation.json.ActionJson;
import com.actemium.marshall.serialisation.json.Pair;

@SuppressWarnings("rawtypes")
public class ActionJsonDictionaryType<T extends Map> extends ActionJson<T> {

	public ActionJsonDictionaryType(Class<T> type, String nom) throws InstantiationException, IllegalAccessException {
		super(type, nom);
		obj = type.newInstance();
	}
	
	@Override
	protected Class<?> getType(String clefEnCours) {
		return null;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected <W> void integreObjet(String nomAttribut, W objet) {
		for(Object o : (ArrayList<?>)objet){
			Pair entry = (Pair)o;
			Object key = entry.__map__clef;
			Object value = entry.__map__valeur;
			obj.put(key, value);
		}
	}

}
