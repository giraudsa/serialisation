package giraudsa.marshall.deserialisation.text.json.actions;

import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.json.ActionJson;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import utils.TypeExtension;
import utils.champ.Champ;

public class ActionJsonObject<T> extends ActionJson<T> {

	private Champ champId;
	private Map<Champ, Object> dicoChampToValue;
	
	public ActionJsonObject(Class<T> type, String nom) {
		super(type, nom);
		champId = TypeExtension.getChampId(type);
		dicoChampToValue = new HashMap<>();
	}

	@Override
	protected Class<?> getType(String clefEnCours) {
		Champ champ = TypeExtension.getChampByName(type, clefEnCours);
		if (champ.isSimple) return TypeExtension.getTypeEnveloppe(champ.valueType);//on renvoie Integer à la place de int, Double au lieu de double, etc...
		return null;
	}
	
	@Override
	protected <W> void integreObjet(String nomAttribut, W objet){
		Champ champ = TypeExtension.getChampByName(type, nomAttribut);
		dicoChampToValue.put(champ, objet);
	}
	
	@Override
	protected <U> void construitObjet(Unmarshaller<U> um) throws InstantiationException, IllegalAccessException {
		String id = dicoChampToValue.get(champId).toString();
		obj = getObject(um, id, type, champId.isFakeId());
		for(Entry<Champ, Object> entry : dicoChampToValue.entrySet()){
			Champ champ = entry.getKey();
			if (!champ.isFakeId()){
				if(!Modifier.isFinal(champ.info.getModifiers())){//on ne modifie pas les attributs finaux
					champ.set(obj, entry.getValue());
				}
			}
		}
	}

}
