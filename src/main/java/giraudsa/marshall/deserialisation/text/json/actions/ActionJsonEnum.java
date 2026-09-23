package giraudsa.marshall.deserialisation.text.json.actions;

import java.util.Collections;
import java.util.Map;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.json.ActionJson;
import giraudsa.marshall.deserialisation.text.json.JsonUnmarshaller;
import utils.Constants;
import utils.TypeExtension;

@SuppressWarnings("rawtypes")
public class ActionJsonEnum<T extends Enum> extends ActionJson<T> {
	public static ActionAbstrait<Enum> getInstance() {
		return new ActionJsonEnum<>(Enum.class, null);
	}

	private Map<String, T> dicoStringEnumToObjEnum = Collections.emptyMap();

	private ActionJsonEnum(final Class<T> type, final JsonUnmarshaller<?> jsonUnmarshaller) {
		super(type, jsonUnmarshaller);
		if (type != Enum.class)
			dicoStringEnumToObjEnum = TypeExtension.getEnumParNom(type);
	}

	@Override
	protected void construitObjet() {
		// les instances des enum sont déjà construit au chargement de la jvm
	}

	@Override
	public <U extends T> ActionAbstrait<U> getNewInstance(final Class<U> type, final Unmarshaller unmarshaller) {
		return new ActionJsonEnum<>(type, (JsonUnmarshaller<?>) unmarshaller);
	}

	@Override
	protected Class<?> getTypeAttribute(final String nomAttribut) {
		if (Constants.VALEUR.equals(nomAttribut))
			return type;
		return null;
	}

	@Override
	protected <W> void integreObjet(final String nomAttribut, final W objet) {
		obj = objet;
	}

	@Override
	protected void rempliData(final String donnees) {
		obj = dicoStringEnumToObjEnum.get(donnees);
	}

}