package giraudsa.marshall.deserialisation.text.xml.actions;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.xml.XmlUnmarshaller;

public class ActionXmlString extends ActionXmlSimpleComportement<String> {

	public static ActionAbstrait<String> getInstance() {
		return new ActionXmlString(String.class, null);
	}

	private ActionXmlString(final Class<String> type, final XmlUnmarshaller<?> xmlUnmarshaller) {
		super(type, xmlUnmarshaller);
	}

	@Override
	protected void construitObjet() {
		obj = unescapeXml(sb.toString());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <U extends String> ActionAbstrait<U> getNewInstance(final Class<U> type, final Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionXmlString(String.class, (XmlUnmarshaller<?>) unmarshaller);
	}

}
