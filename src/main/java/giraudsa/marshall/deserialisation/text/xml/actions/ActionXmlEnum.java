package giraudsa.marshall.deserialisation.text.xml.actions;

import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.xml.ActionXml;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class ActionXmlEnum<T extends Enum> extends ActionXml<T>  {

	Map<String, T> dicoStringEnumToObjEnum = new HashMap<>();
	StringBuilder sb = new StringBuilder();

	@SuppressWarnings("unchecked")
	public ActionXmlEnum(Class<T> type, String nom) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		super(type, nom);
		Method values = type.getDeclaredMethod("values");
		T[] listeEnum = (T[]) values.invoke(null);
		for(T objEnum : listeEnum){
			dicoStringEnumToObjEnum.put(objEnum.toString(), objEnum);
		}
	}

	@Override
	protected void rempliData(String donnees) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		sb.append(donnees);
	}
	
	@Override protected <U> void construitObjet(Unmarshaller<U> um) throws InstantiationException, IllegalAccessException {
		obj = dicoStringEnumToObjEnum.get(sb.toString());
	}
}
