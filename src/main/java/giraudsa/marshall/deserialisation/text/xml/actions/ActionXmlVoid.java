package giraudsa.marshall.deserialisation.text.xml.actions;

import java.lang.reflect.InvocationTargetException;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.xml.XmlUnmarshaller;
import giraudsa.marshall.exception.UnmarshallExeption;

public class ActionXmlVoid extends ActionXmlSimpleComportement<Void> {

	private ActionXmlVoid(Class<Void> type, XmlUnmarshaller<?> xmlUnmarshaller) {
		super(type, xmlUnmarshaller);
	}

	public static ActionAbstrait<Void> getInstance() {	
		return new ActionXmlVoid(Void.class, null);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <U extends Void> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionXmlVoid(Void.class, (XmlUnmarshaller<?>)unmarshaller);
	}
	
	@Override protected <W> void integreObjet(String nomAttribut, W objet) {
		//rien à faire avec un objet null
	}
	
	@Override protected void rempliData(String donnees) {
		//rien à faire avec un objet null
	}
	
	@Override
	protected void construitObjet() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, UnmarshallExeption {
		obj = null;
	}
	
	@Override
	protected Void getObjet() {
		return null;
	}
}
