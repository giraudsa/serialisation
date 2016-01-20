package giraudsa.marshall.deserialisation.text.xml.actions;


import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.xml.XmlUnmarshaller;

import java.util.UUID;

public class ActionXmlUUID extends ActionXmlSimpleComportement<UUID>{

	
	private ActionXmlUUID(Class<UUID> type, XmlUnmarshaller<?> xmlUnmarshaller) {
		super(type, xmlUnmarshaller);
	}

	public static ActionAbstrait<UUID> getInstance() {	
		return new ActionXmlUUID(UUID.class, null);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <U extends UUID> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionXmlUUID(UUID.class, (XmlUnmarshaller<?>)unmarshaller);
	}
	
	@Override protected void construitObjet() {
		obj = UUID.fromString(sb.toString());
	}
}
