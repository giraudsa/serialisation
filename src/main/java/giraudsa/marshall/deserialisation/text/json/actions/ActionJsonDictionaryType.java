package giraudsa.marshall.deserialisation.text.json.actions;

import giraudsa.marshall.deserialisation.text.json.ActionJson;
import giraudsa.marshall.deserialisation.text.json.JsonUnmarshaller;
import giraudsa.marshall.serialisation.text.json.Pair;

import java.util.ArrayList;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class ActionJsonDictionaryType<T extends Map> extends ActionJson<T> {

	public ActionJsonDictionaryType(Class<T> type, String nom, JsonUnmarshaller<?> jsonUnmarshaller) throws InstantiationException, IllegalAccessException {
		super(type, nom, jsonUnmarshaller);
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
