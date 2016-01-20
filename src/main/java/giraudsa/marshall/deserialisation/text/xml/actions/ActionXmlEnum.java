package giraudsa.marshall.deserialisation.text.xml.actions;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.xml.ActionXml;
import giraudsa.marshall.deserialisation.text.xml.XmlUnmarshaller;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("rawtypes")
public class ActionXmlEnum<T extends Enum> extends ActionXml<T>  {
	private static final Logger LOGGER = LoggerFactory.getLogger(ActionXmlEnum.class);
	private Map<String, T> dicoStringEnumToObjEnum = new HashMap<>();
	private StringBuilder sb = new StringBuilder();
	
	@SuppressWarnings("unchecked")
	private ActionXmlEnum(Class<T> type, XmlUnmarshaller<?> xmlUnmarshaller) {
		super(type, xmlUnmarshaller);
		if(type == Enum.class)
			return;
		Method values;
		try {
			values = type.getDeclaredMethod("values");
			T[] listeEnum = (T[]) values.invoke(null);
			for(T objEnum : listeEnum){
				dicoStringEnumToObjEnum.put(objEnum.toString(), objEnum);
			}
		} catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			LOGGER.error("T n'est pas un Enum... étrange", e);
		} 
	}

	public static ActionAbstrait<Enum> getInstance() {	
		return new ActionXmlEnum<>(Enum.class, null);
	}
	
	@Override
	public <U extends T> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return new ActionXmlEnum<>(type, (XmlUnmarshaller<?>)unmarshaller);
	}

	@Override
	protected void rempliData(String donnees) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
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
