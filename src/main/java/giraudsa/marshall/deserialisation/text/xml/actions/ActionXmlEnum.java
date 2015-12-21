package giraudsa.marshall.deserialisation.text.xml.actions;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.xml.ActionXml;
import giraudsa.marshall.deserialisation.text.xml.XmlUnmarshaller;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class ActionXmlEnum<T extends Enum> extends ActionXml<T>  {

	private Map<String, T> dicoStringEnumToObjEnum = new HashMap<>();
	private StringBuilder sb = new StringBuilder();
	
	public static ActionAbstrait<?> getInstance(XmlUnmarshaller<?> u) {	
		return new ActionXmlEnum<Enum>(Enum.class, u);
	}
	
	@Override
	public <U extends T> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return new ActionXmlEnum<U>(type, (XmlUnmarshaller<?>)unmarshaller);
	}

	@SuppressWarnings("unchecked")
	private ActionXmlEnum(Class<T> type, XmlUnmarshaller<?> xmlUnmarshaller) {
		super(type, xmlUnmarshaller);
		Method values;
		try {
			values = type.getDeclaredMethod("values");
			T[] listeEnum = (T[]) values.invoke(null);
			for(T objEnum : listeEnum){
				dicoStringEnumToObjEnum.put(objEnum.toString(), objEnum);
			}
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void rempliData(String donnees) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		sb.append(donnees);
	}
	
	@Override protected void construitObjet(){
		obj = dicoStringEnumToObjEnum.get(sb.toString());
	}

	@Override
	protected <W> void integreObjet(String nomAttribut, W objet) {
		//rien a faire
	}
}
