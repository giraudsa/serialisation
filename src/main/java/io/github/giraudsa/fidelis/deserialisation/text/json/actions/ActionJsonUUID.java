package io.github.giraudsa.fidelis.deserialisation.text.json.actions;

import java.util.UUID;

import io.github.giraudsa.fidelis.deserialisation.ActionAbstrait;
import io.github.giraudsa.fidelis.deserialisation.Unmarshaller;
import io.github.giraudsa.fidelis.deserialisation.text.json.JsonUnmarshaller;
import io.github.giraudsa.fidelis.utils.Constants;

public class ActionJsonUUID extends ActionJsonSimpleComportement<UUID> {

	public static ActionAbstrait<UUID> getInstance() {
		return new ActionJsonUUID(UUID.class, null);
	}

	private ActionJsonUUID(final Class<UUID> type, final JsonUnmarshaller<?> jsonUnmarshaller) {
		super(type, jsonUnmarshaller);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <U extends UUID> ActionAbstrait<U> getNewInstance(final Class<U> type, final Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionJsonUUID(UUID.class, (JsonUnmarshaller<?>) unmarshaller);
	}

	@Override
	protected Class<?> getTypeAttribute(final String nomAttribut) {
		if (Constants.VALEUR.equals(nomAttribut))
			return UUID.class;
		return null;
	}

	@Override
	protected <W> void integreObjet(final String nomAttribut, final W objet) {
		if (objet instanceof String)
			obj = UUID.fromString((String) objet);
		else if (objet instanceof UUID)
			obj = objet;
	}

	@Override
	protected void rempliData(final String donnees) {
		obj = UUID.fromString(donnees);
	}
}
