package giraudsa.marshall.deserialisation.text.xml.actions;


import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

public class ActionXmlUUID extends ActionXmlSimpleComportement<UUID>{

	public ActionXmlUUID(Class<UUID> type, String nom) {
		super(type, nom);
	}
	
	@Override
	protected void rempliData(String donnees) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		obj = UUID.fromString(donnees);
	}

}
