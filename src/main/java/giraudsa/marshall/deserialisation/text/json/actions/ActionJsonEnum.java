package giraudsa.marshall.deserialisation.text.json.actions;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.json.ActionJson;
import giraudsa.marshall.deserialisation.text.json.JsonUnmarshaller;
import utils.Constants;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class ActionJsonEnum<T extends Enum> extends ActionJson<T>  {

	private Map<String, T> dicoStringEnumToObjEnum = new HashMap<>();
	

	public static ActionAbstrait<Enum> getInstance(JsonUnmarshaller<?> jsonUnmarshaller){
		return new ActionJsonEnum<>(Enum.class, jsonUnmarshaller);
	}
	@Override
	public <U extends T> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return new ActionJsonEnum<>(type, (JsonUnmarshaller<?>)unmarshaller);
	}


	@SuppressWarnings("unchecked")
	private ActionJsonEnum(Class<T> type, JsonUnmarshaller<?> jsonUnmarshaller){
		super(type, jsonUnmarshaller);
		if(type != Enum.class){
			Method values;
			try {
				values = type.getDeclaredMethod("values");
				T[] listeEnum = (T[]) values.invoke(null);
				for(T objEnum : listeEnum){
					dicoStringEnumToObjEnum.put(objEnum.toString(), objEnum);
				}
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void rempliData(String donnees) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		obj = dicoStringEnumToObjEnum.get(donnees);
	}
	
	@Override protected Class<?> getTypeAttribute(String nomAttribut) {
		if(Constants.VALEUR.equals(nomAttribut)) return type;
		return null;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected <W> void integreObjet(String nomAttribut, W objet) {
		obj = (T) objet;
	}

	@Override
	protected void construitObjet() throws InstantiationException, IllegalAccessException {
		// TODO Auto-generated method stub
		
	}
	
}