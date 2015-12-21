package giraudsa.marshall.deserialisation.text.json.actions;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.json.ActionJson;
import giraudsa.marshall.deserialisation.text.json.JsonUnmarshaller;
import utils.Constants;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class ActionJsonDictionaryType<T extends Map> extends ActionJson<T> {
	
	private Object clefTampon = null;
	
	public static ActionAbstrait<Map> getInstance(JsonUnmarshaller<?> unmarshaller){
		return new ActionJsonDictionaryType<>(Map.class, unmarshaller);
	}
	
	@Override
	public <U extends T> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return new ActionJsonDictionaryType<>(type, (JsonUnmarshaller<?>)unmarshaller);
	}

	private ActionJsonDictionaryType(Class<T> type, JsonUnmarshaller<?> jsonUnmarshaller){
		super(type, jsonUnmarshaller);
		try {
			obj = type.newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override protected Class<?> getTypeAttribute(String nomAttribut) {
		if(Constants.VALEUR.equals(nomAttribut)) return ArrayList.class;
		return null;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected <W> void integreObjet(String nomAttribut, W objet) {
		for(Object o : (ArrayList<?>)objet){
			if(clefTampon == null){
				clefTampon = o;
			}else{
				((Map)obj).put(clefTampon, o);
				clefTampon = null;
			}
		}
	}


	@Override
	protected void construitObjet() throws InstantiationException, IllegalAccessException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void rempliData(String donnees) throws ParseException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		// TODO Auto-generated method stub
		
	}

}
