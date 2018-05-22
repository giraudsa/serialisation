package giraudsa.marshall.deserialisation.text.xml.actions;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.xml.XmlUnmarshaller;

public class ActionXmlVoid extends ActionXmlSimpleComportement<Void> {

	public static ActionAbstrait<Void> getInstance() {
		return new ActionXmlVoid(Void.class, null);
	}

	private ActionXmlVoid(final Class<Void> type, final XmlUnmarshaller<?> xmlUnmarshaller) {
		super(type, xmlUnmarshaller);
	}

	@Override
	protected void construitObjet() {
		obj = null;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <U extends Void> ActionAbstrait<U> getNewInstance(final Class<U> type, final Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionXmlVoid(Void.class, (XmlUnmarshaller<?>) unmarshaller);
	}

	@Override
	protected Void getObjet() {
		return null;
	}

	@Override
	protected <W> void integreObjet(final String nomAttribut, final W objet) {
		// rien à faire avec un objet null
	}

	@Override
	protected void rempliData(final String donnees) {
		// rien à faire avec un objet null
	}
}
