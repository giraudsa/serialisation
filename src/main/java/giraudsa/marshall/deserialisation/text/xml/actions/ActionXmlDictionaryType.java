package giraudsa.marshall.deserialisation.text.xml.actions;

import giraudsa.marshall.deserialisation.text.xml.ActionXml;

import java.util.Map;

@SuppressWarnings("rawtypes")
public class ActionXmlDictionaryType<T extends Map> extends ActionXml<T> {
	private Object keyTampon;
	
	public ActionXmlDictionaryType(Class<T> type, String nom) throws InstantiationException, IllegalAccessException {
		super(type, nom);
		obj = type.newInstance();
	}

	@SuppressWarnings("unchecked") @Override
	protected <W> void integreObjet(String nomAttribut, W objet) {
		if(keyTampon == null) keyTampon = objet;
		obj.put(keyTampon, objet);
		keyTampon = null;
	}

}
