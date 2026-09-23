package giraudsa.marshall.deserialisation.text.xml.actions;

import java.util.Collections;
import java.util.Map;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.xml.ActionXml;
import giraudsa.marshall.deserialisation.text.xml.XmlUnmarshaller;
import utils.TypeExtension;

@SuppressWarnings("rawtypes")
public class ActionXmlEnum<T extends Enum> extends ActionXml<T> {
	public static ActionAbstrait<Enum> getInstance() {
		return new ActionXmlEnum<>(Enum.class, null);
	}

	private Map<String, T> dicoStringEnumToObjEnum = Collections.emptyMap();

	private final StringBuilder sb = new StringBuilder();

	private ActionXmlEnum(final Class<T> type, final XmlUnmarshaller<?> xmlUnmarshaller) {
		super(type, xmlUnmarshaller);
		if (type == Enum.class)
			return;
		dicoStringEnumToObjEnum = TypeExtension.getEnumParNom(type);
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
