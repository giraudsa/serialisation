package giraudsa.marshall.deserialisation.text.json.actions;

import giraudsa.marshall.deserialisation.text.json.ActionJson;
import giraudsa.marshall.deserialisation.text.json.JsonUnmarshaller;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class ActionJsonEnum<T extends Enum> extends ActionJson<T>  {

	Map<String, T> dicoStringEnumToObjEnum = new HashMap<>();
	
	@SuppressWarnings("unchecked")
	public ActionJsonEnum(Class<T> type, String nom, JsonUnmarshaller<?> jsonUnmarshaller) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		super(type, nom, jsonUnmarshaller);
		Method values = type.getDeclaredMethod("values");
		T[] listeEnum = (T[]) values.invoke(null);
		for(T objEnum : listeEnum){
			dicoStringEnumToObjEnum.put(objEnum.toString(), objEnum);
		}
	}

	@Override
	protected void rempliData(String donnees) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		obj = dicoStringEnumToObjEnum.get(donnees);
	}
	
	@Override
	protected Class<?> getType(String clefEnCours) {
		return type;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected <W> void integreObjet(String nomAttribut, W objet) {
		obj = (T) objet;
	}
	
}