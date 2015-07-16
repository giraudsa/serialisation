package com.actemium.marshall.deserialisation.xml.actions;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import utils.TypeExtension;
import utils.champ.Champ;

import com.actemium.marshall.deserialisation.Unmarshaller;
import com.actemium.marshall.deserialisation.xml.ActionXml;


public class ActionXmlObject<T> extends ActionXml<T> {
	private Champ champId;
	private Map<Champ, Object> dicoChampToValue;
	
	public ActionXmlObject(Class<T> type, String nom) {
		super(type, nom);
		champId = TypeExtension.getChampId(type);
		dicoChampToValue = new HashMap<>();
	}

	@Override
	protected <U> void construitObjet(Unmarshaller<U> unmarshaller) throws InstantiationException, IllegalAccessException {
		obj = getObject(unmarshaller, dicoChampToValue.get(champId).toString(), type);
		for(Entry<Champ, Object> entry : dicoChampToValue.entrySet()){
			Champ champ = entry.getKey();
			if (!champ.isFakeId()){
				if(!Modifier.isFinal(champ.info.getModifiers())){//on ne modifie pas les attributs finaux
					champ.set(obj, entry.getValue());
				}
			}
		}
	}
	
	@Override
	protected <W> void integreObjet(String nomAttribut, W objet){
		Champ champ = TypeExtension.getChampByName(type, nomAttribut);
		dicoChampToValue.put(champ, objet);
	}

}
