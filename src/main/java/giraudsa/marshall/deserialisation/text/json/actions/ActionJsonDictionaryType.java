package giraudsa.marshall.deserialisation.text.json.actions;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.json.ActionJson;
import giraudsa.marshall.deserialisation.text.json.JsonUnmarshaller;
import utils.Constants;
import utils.champ.FakeChamp;
import utils.champ.FieldInformations;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("rawtypes")
public class ActionJsonDictionaryType<T extends Map> extends ActionJson<T> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionJsonDictionaryType.class);
	private Object clefTampon = null;
	
	private FakeChamp fakeChampKey;
	private FakeChamp fakeChampValue;
	private ActionJsonDictionaryType(Class<T> type, JsonUnmarshaller<?> jsonUnmarshaller){
		super(type, jsonUnmarshaller);
		if(!type.isInterface()){
			try {
				obj = type.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				obj = new HashMap<>();
				LOGGER.error("instanciation impossible pour " + type.getName(), e);
			}
		}
	}

	private FakeChamp getFakeChamp(){
		if(clefTampon == null){
			if (fakeChampKey == null){
				Type[] types = fieldInformations.getParametreType();
				Type typeGeneric = Object.class;
				if(types != null && types.length > 0) 
					typeGeneric = types[0];
				fakeChampKey = new FakeChamp("K", typeGeneric, fieldInformations.getRelation());
			}
			return fakeChampKey;
		}
		if(fakeChampValue == null){
			Type[] types = fieldInformations.getParametreType();
			Type typeGeneric = Object.class;
			if(types != null && types.length > 1)
				typeGeneric = types[1];
			fakeChampValue = new FakeChamp("V", typeGeneric, fieldInformations.getRelation());
		}
		return fakeChampValue;
	}
	
	public static ActionAbstrait<Map> getInstance(){
		return new ActionJsonDictionaryType<>(Map.class, null);
	}
	
	@Override
	public <U extends T> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return new ActionJsonDictionaryType<>(type, (JsonUnmarshaller<?>)unmarshaller);
	}

	@Override protected Class<?> getTypeAttribute(String nomAttribut) {
		if(Constants.VALEUR.equals(nomAttribut)) 
			return ArrayList.class;
		return getFakeChamp().getValueType();
	}
	
	@Override
	protected FieldInformations getFieldInformationSpecialise(String nomAttribut) {
		if(Constants.VALEUR.equals(nomAttribut)) 
			return fieldInformations;
		return getFakeChamp();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected <W> void integreObjet(String nomAttribut, W objet) {
		if(nomAttribut == null){
			if(clefTampon == null){
				clefTampon = objet;
			}else{
				((Map)obj).put(clefTampon, objet);
				clefTampon = null;
			}

		}else{
			for(Object o : (ArrayList<?>)objet){
				if(clefTampon == null){
					clefTampon = o;
				}else{
					((Map)obj).put(clefTampon, o);
					clefTampon = null;
				}
			}
		}
	}


	@Override
	protected void construitObjet() {
		//l'objet est construit à l'instanciation de la classe.
	}

	@Override
	protected void rempliData(String donnees) {
		//l'objet est construit à l'instanciation de la classe.
	}

}
