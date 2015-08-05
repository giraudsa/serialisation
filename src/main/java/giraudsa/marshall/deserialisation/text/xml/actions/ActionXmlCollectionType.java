package giraudsa.marshall.deserialisation.text.xml.actions;

import giraudsa.marshall.deserialisation.text.xml.ActionXml;
import giraudsa.marshall.deserialisation.text.xml.XmlUnmarshaller;

import java.util.Collection;

@SuppressWarnings("rawtypes")
public class ActionXmlCollectionType<T extends Collection> extends ActionXml<T> {
    
	public ActionXmlCollectionType(Class<T> type, String nom, XmlUnmarshaller<?> xmlUnmarshaller) throws InstantiationException, IllegalAccessException {
		super(type, nom, xmlUnmarshaller);
		obj = type.newInstance();
	}

	
	@SuppressWarnings("unchecked")	@Override
	protected <W> void integreObjet(String nomAttribut, W objet) {
		obj.add(objet);
	}

}
