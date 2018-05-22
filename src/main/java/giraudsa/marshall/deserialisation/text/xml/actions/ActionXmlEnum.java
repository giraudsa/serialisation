package giraudsa.marshall.deserialisation.text.xml.actions;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.xml.ActionXml;
import giraudsa.marshall.deserialisation.text.xml.XmlUnmarshaller;

@SuppressWarnings("rawtypes")
public class ActionXmlEnum<T extends Enum> extends ActionXml<T> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ActionXmlEnum.class);

	public static ActionAbstrait<Enum> getInstance() {
		return new ActionXmlEnum<>(Enum.class, null);
	}

	private final Map<String, T> dicoStringEnumToObjEnum = new HashMap<>();

	private final StringBuilder sb = new StringBuilder();

	@SuppressWarnings("unchecked")
	private ActionXmlEnum(final Class<T> type, final XmlUnmarshaller<?> xmlUnmarshaller) {
		super(type, xmlUnmarshaller);
		if (type == Enum.class)
			return;
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

	@Override
	protected void construitObjet() {
		obj = dicoStringEnumToObjEnum.get(sb.toString());
	}

	@Override
	public <U extends T> ActionAbstrait<U> getNewInstance(final Class<U> type, final Unmarshaller unmarshaller) {
		return new ActionXmlEnum<>(type, (XmlUnmarshaller<?>) unmarshaller);
	}

	@Override
	protected <W> void integreObjet(final String nomAttribut, final W objet) {
		// rien a faire
	}

	@Override
	protected void rempliData(final String donnees) {
		sb.append(donnees);
	}
}
