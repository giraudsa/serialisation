package giraudsa.marshall.deserialisation.text.xml.actions;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.xml.XmlUnmarshaller;

import java.util.ArrayList;
import java.util.Collection;

@SuppressWarnings("rawtypes")
public class ActionXmlCollectionType<CollectionType extends Collection> extends ActionXmlComplexeObject<CollectionType> {

    public static ActionAbstrait<?> getInstance(XmlUnmarshaller<?> u) {	
		return new ActionXmlCollectionType<Collection>(Collection.class, u);
	}
	
	@Override
	public <U extends CollectionType> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return new ActionXmlCollectionType<U>(type, (XmlUnmarshaller<?>) unmarshaller);
	}

	
	private ActionXmlCollectionType(Class<CollectionType> type, XmlUnmarshaller<?> unmarshaller) {
		super(type, unmarshaller);
		Class<?> _type = type;
		if(type.getName().toLowerCase().indexOf("hibernate") != -1 || type.isInterface()) _type = ArrayList.class;
		try {
			obj = _type.newInstance();
		} catch (InstantiationException e) {
			obj = new ArrayList<>();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	
	@SuppressWarnings("unchecked")	@Override
	protected <W> void integreObjet(String nomAttribut, W objet) {
		((Collection)obj).add(objet);
	}

	@Override
	protected void construitObjet() {
		//rien à faire
	}

}
