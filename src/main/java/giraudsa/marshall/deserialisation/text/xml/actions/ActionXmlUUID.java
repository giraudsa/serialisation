package giraudsa.marshall.deserialisation.text.xml.actions;


import giraudsa.marshall.deserialisation.text.xml.XmlUnmarshaller;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

public class ActionXmlUUID extends ActionXmlSimpleComportement<UUID>{

	public ActionXmlUUID(Class<UUID> type, String nom, XmlUnmarshaller<?> xmlUnmarshaller) {
		super(type, nom, xmlUnmarshaller);
	}
	
	@Override
	protected void rempliData(String donnees) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		obj = UUID.fromString(donnees);
	}

}
