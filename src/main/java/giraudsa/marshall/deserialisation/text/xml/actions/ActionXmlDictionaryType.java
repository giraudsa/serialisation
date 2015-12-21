package giraudsa.marshall.deserialisation.text.xml.actions;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.xml.XmlUnmarshaller;

import java.util.Map;

@SuppressWarnings("rawtypes")
public class ActionXmlDictionaryType<T extends Map> extends ActionXmlComplexeObject<T> {
	private Object keyTampon;
	
	
	public static ActionAbstrait<?> getInstance(XmlUnmarshaller<?> u) {	
		return new ActionXmlDictionaryType<Map>(Map.class, u);
	}
	
	@Override
	public <U extends T> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return new ActionXmlDictionaryType<U>(type, (XmlUnmarshaller<?>)unmarshaller);
	}
	
	private ActionXmlDictionaryType(Class<T> type, XmlUnmarshaller<?> xmlUnmarshaller){
		super(type, xmlUnmarshaller);
		try {
			obj = type.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked") @Override
	protected <W> void integreObjet(String nomAttribut, W objet) {
		if(keyTampon == null){
			keyTampon = objet;
		}else{
			((Map)obj).put(keyTampon, objet);
			keyTampon = null;
		}
	}

	@Override
	protected void construitObjet() {
		//rien a faire
	}

}
