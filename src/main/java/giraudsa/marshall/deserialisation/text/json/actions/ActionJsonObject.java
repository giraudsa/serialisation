package giraudsa.marshall.deserialisation.text.json.actions;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.json.ActionJson;
import giraudsa.marshall.deserialisation.text.json.JsonUnmarshaller;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import utils.TypeExtension;
import utils.champ.Champ;
import utils.champ.FieldInformations;

public class ActionJsonObject<T> extends ActionJson<T> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ActionJsonObject.class);
	private Champ champId;
	private Map<Champ, Object> dicoChampToValue;
	
	private ActionJsonObject(Class<T> type, JsonUnmarshaller<?> jsonUnmarshaller) {
		super(type, jsonUnmarshaller);
		champId = TypeExtension.getChampId(type);
		dicoChampToValue = new HashMap<Champ, Object>();
	}

	public static ActionAbstrait<Object> getInstance(){
		return new ActionJsonObject<Object>(Object.class, null);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public <U extends T> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return new ActionJsonObject<U>(type, (JsonUnmarshaller<?>)unmarshaller);
	}

	
	@Override protected Class<?> getTypeAttribute(String nomAttribut) {
		Champ champ = TypeExtension.getChampByName(type, nomAttribut);
		if (champ.isSimple())
			return TypeExtension.getTypeEnveloppe(champ.getValueType());//on renvoie Integer à la place de int, Double au lieu de double, etc...
		return champ.getValueType();
	}
	
	@Override
	protected <W> void integreObjet(String nomAttribut, W objet){
		Champ champ = TypeExtension.getChampByName(type, nomAttribut);
		dicoChampToValue.put(champ, objet);
	}
	
	@Override
	protected void construitObjet() throws InstantiationException, IllegalAccessException {
		String id = dicoChampToValue.get(champId).toString();
		obj = getObject(id, type);
		if (obj == null) 
			return;
		for(Entry<Champ, Object> entry : dicoChampToValue.entrySet()){
			Champ champ = entry.getKey();
			if (!champ.isFakeId())
				champ.set(obj, entry.getValue());
		}
	}
	
	@Override 
    protected FieldInformations getFieldInformationSpecialise(String nomAttribut){
		return TypeExtension.getChampByName(type, nomAttribut);
	}


	@Override
	protected void rempliData(String donnees) throws ParseException, InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		LOGGER.error("on est pas supposé avoir de données avec un objet.");
		//rien a faire avec un objet, il n'y a pas de data
	}

}
