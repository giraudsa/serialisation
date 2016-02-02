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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("rawtypes")
public class ActionJsonEnum<T extends Enum> extends ActionJson<T>  {
	private static final Logger LOGGER = LoggerFactory.getLogger(ActionJsonEnum.class);
	private Map<String, T> dicoStringEnumToObjEnum = new HashMap<String, T>();
	

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
			} catch (Exception e) {
				LOGGER.error("T n'est pas un Enum... étrange", e);
			} 
		}
	}
	public static ActionAbstrait<Enum> getInstance(){
		return new ActionJsonEnum<Enum>(Enum.class, null);
	}
	@Override
	public <U extends T> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return new ActionJsonEnum<U>(type, (JsonUnmarshaller<?>)unmarshaller);
	}


	@Override
	protected void rempliData(String donnees) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		obj = dicoStringEnumToObjEnum.get(donnees);
	}
	
	@Override protected Class<?> getTypeAttribute(String nomAttribut) {
		if(Constants.VALEUR.equals(nomAttribut))
			return type;
		return null;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected <W> void integreObjet(String nomAttribut, W objet) {
		obj = (T) objet;
	}

	@Override
	protected void construitObjet() throws InstantiationException, IllegalAccessException {
		//les instances des enum sont déjà construit au chargement de la jvm
	}
	
}