package giraudsa.marshall.deserialisation.text.json.actions;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.json.JsonUnmarshaller;
import utils.Constants;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

public class ActionJsonUUID extends ActionJsonSimpleComportement<UUID> {

	private ActionJsonUUID(Class<UUID> type, JsonUnmarshaller<?> jsonUnmarshaller) {
		super(type, jsonUnmarshaller);
	}

	public static ActionAbstrait<UUID> getInstance(){
		return new ActionJsonUUID(UUID.class, null);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <U extends UUID> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionJsonUUID(UUID.class, (JsonUnmarshaller<?>)unmarshaller);
	}
	
	@Override protected Class<?> getTypeAttribute(String nomAttribut) {
		if(Constants.VALEUR.equals(nomAttribut))
			return UUID.class;
		return null;
	}
	
	@Override
	protected <W> void integreObjet(String nomAttribut, W objet) {
		if(objet instanceof String) 
			obj = UUID.fromString((String) objet);
		else if (objet instanceof UUID) 
			obj = (UUID)objet;
	}
	
	@Override
	protected void rempliData(String donnees) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		obj = UUID.fromString(donnees);
	}
}
