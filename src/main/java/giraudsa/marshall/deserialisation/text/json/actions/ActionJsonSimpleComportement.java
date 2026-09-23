package giraudsa.marshall.deserialisation.text.json.actions;

import java.lang.reflect.InvocationTargetException;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.json.ActionJson;
import giraudsa.marshall.deserialisation.text.json.JsonUnmarshaller;
import giraudsa.marshall.exception.InstanciationException;
import utils.Constants;

public class ActionJsonSimpleComportement<T> extends ActionJson<T> {

	@SuppressWarnings("unchecked")
	public static <U> ActionAbstrait<U> getInstance() {
		return (ActionAbstrait<U>) new ActionJsonSimpleComportement<>(Object.class, null);
	}

	protected ActionJsonSimpleComportement(final Class<T> type, final JsonUnmarshaller<?> jsonUnmarshaller) {
		super(type, jsonUnmarshaller);
	}

	@Override
	protected void construitObjet() {
		// rien a faire
	}

	@SuppressWarnings("rawtypes")
	@Override
	public <U extends T> ActionAbstrait<U> getNewInstance(final Class<U> type, final Unmarshaller unmarshaller) {
		return new ActionJsonSimpleComportement<>(type, (JsonUnmarshaller<?>) unmarshaller);
	}

	@Override
	protected Class<?> getTypeAttribute(final String nomAttribut) {
		if (Constants.VALEUR.equals(nomAttribut))
			return type;
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected <W> void integreObjet(final String nomAttribut, final W objet) {
		// valeur enveloppée {"__type":..., "__valeur":...} quand le type n'est pas devinable
		if (Constants.VALEUR.equals(nomAttribut))
			obj = (T) objet;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void rempliData(final String donnees) throws InstanciationException {
		if (type == Character.class) { // pas de constructeur Character(String)
			if (donnees.length() != 1)
				throw new InstanciationException("un caractère est attendu au lieu de \"" + donnees + "\"");
			obj = (T) Character.valueOf(donnees.charAt(0));
			return;
		}
		try {
			obj = type.getConstructor(String.class).newInstance(donnees);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new InstanciationException(
					"impossible de trouver un constructeur avec un string pour le type " + type.getName(), e);
		}
	}

}
