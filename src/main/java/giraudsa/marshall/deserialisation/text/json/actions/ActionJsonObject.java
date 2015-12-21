package giraudsa.marshall.deserialisation.text.json.actions;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.json.ActionJson;
import giraudsa.marshall.deserialisation.text.json.JsonUnmarshaller;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import utils.TypeExtension;
import utils.champ.Champ;

public class ActionJsonObject<T> extends ActionJson<T> {

	private Champ champId;
	private Map<Champ, Object> dicoChampToValue;
	
	public static ActionAbstrait<?> getInstance(JsonUnmarshaller<?> jsonUnmarshaller){
		return new ActionJsonObject<>(Object.class, jsonUnmarshaller);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public <U extends T> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return new ActionJsonObject<>(type, (JsonUnmarshaller<?>)unmarshaller);
	}

	
	private ActionJsonObject(Class<T> type, JsonUnmarshaller<?> jsonUnmarshaller) {
		super(type, jsonUnmarshaller);
		champId = TypeExtension.getChampId(type);
		dicoChampToValue = new HashMap<>();
	}

	@Override protected Class<?> getTypeAttribute(String nomAttribut) {
		Champ champ = TypeExtension.getChampByName(type, nomAttribut);
		if (champ.isSimple) return TypeExtension.getTypeEnveloppe(champ.valueType);//on renvoie Integer à la place de int, Double au lieu de double, etc...
		return champ.valueType;
	}
	
	@Override
	protected <W> void integreObjet(String nomAttribut, W objet){
		Champ champ = TypeExtension.getChampByName(type, nomAttribut);
		dicoChampToValue.put(champ, objet);
	}
	
	@Override
	protected void construitObjet() throws InstantiationException, IllegalAccessException {
		String id = dicoChampToValue.get(champId).toString();
		obj = getObject(id, type, champId.isFakeId());
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
	protected void rempliData(String donnees) throws ParseException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		// TODO Auto-generated method stub
		
	}

}
