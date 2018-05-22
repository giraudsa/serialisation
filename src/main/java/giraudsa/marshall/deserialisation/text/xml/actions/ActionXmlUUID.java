package giraudsa.marshall.deserialisation.text.xml.actions;

import java.util.UUID;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.xml.XmlUnmarshaller;

public class ActionXmlUUID extends ActionXmlSimpleComportement<UUID> {

	public static ActionAbstrait<UUID> getInstance() {
		return new ActionXmlUUID(UUID.class, null);
	}

	private ActionXmlUUID(final Class<UUID> type, final XmlUnmarshaller<?> xmlUnmarshaller) {
		super(type, xmlUnmarshaller);
	}

	@Override
	protected void construitObjet() {
		obj = UUID.fromString(sb.toString());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <U extends UUID> ActionAbstrait<U> getNewInstance(final Class<U> type, final Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionXmlUUID(UUID.class, (XmlUnmarshaller<?>) unmarshaller);
	}
}
