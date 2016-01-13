package giraudsa.marshall.deserialisation.text.xml.actions;


import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.xml.XmlUnmarshaller;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

public class ActionXmlUUID extends ActionXmlSimpleComportement<UUID>{

	
	private ActionXmlUUID(Class<UUID> type, XmlUnmarshaller<?> xmlUnmarshaller) {
		super(type, xmlUnmarshaller);
	}

	public static ActionAbstrait<UUID> getInstance(XmlUnmarshaller<?> u) {	
		return new ActionXmlUUID(UUID.class, u);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <U extends UUID> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionXmlUUID(UUID.class, (XmlUnmarshaller<?>)unmarshaller);
	}
	
	@Override
	protected void rempliData(String donnees) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		obj = UUID.fromString(donnees);
	}

}
