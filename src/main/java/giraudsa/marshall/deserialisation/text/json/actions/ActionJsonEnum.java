package giraudsa.marshall.deserialisation.text.json.actions;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.json.ActionJson;
import giraudsa.marshall.deserialisation.text.json.JsonUnmarshaller;
import utils.Constants;

@SuppressWarnings("rawtypes")
public class ActionJsonEnum<T extends Enum> extends ActionJson<T> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ActionJsonEnum.class);

	public static ActionAbstrait<Enum> getInstance() {
		return new ActionJsonEnum<>(Enum.class, null);
	}

	private final Map<String, T> dicoStringEnumToObjEnum = new HashMap<>();

	@SuppressWarnings("unchecked")
	private ActionJsonEnum(final Class<T> type, final JsonUnmarshaller<?> jsonUnmarshaller) {
		super(type, jsonUnmarshaller);
		if (type != Enum.class) {
			Method values;
			try {
				values = type.getDeclaredMethod("values");
				final T[] listeEnum = (T[]) values.invoke(null);
				for (final T objEnum : listeEnum)
					dicoStringEnumToObjEnum.put(objEnum.toString(), objEnum);
			} catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				LOGGER.error("T n'est pas un Enum... étrange", e);
				throw new NullPointerException();
			}
		}
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