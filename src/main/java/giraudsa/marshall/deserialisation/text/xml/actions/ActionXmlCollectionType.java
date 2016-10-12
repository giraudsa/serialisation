package giraudsa.marshall.deserialisation.text.xml.actions;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.xml.XmlUnmarshaller;
import utils.champ.FakeChamp;
import utils.champ.FieldInformations;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("rawtypes")
public class ActionXmlCollectionType<C extends Collection> extends ActionXmlComplexeObject<C> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ActionXmlCollectionType.class);
	private FakeChamp fakeChamp;
	private ActionXmlCollectionType(Class<C> type, XmlUnmarshaller<?> unmarshaller) {
		super(type, unmarshaller);
		Class<?> ttype = type;
		if(type.getName().toLowerCase().indexOf("hibernate") != -1 || type.isInterface())
			ttype = ArrayList.class;
		try {
			obj = ttype.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			LOGGER.debug("impossible de créer une instance de " + ttype.getName(), e);
			obj = new ArrayList<>();
		}
	}
	private FakeChamp getFakeChamp(){
		if(fakeChamp == null){
			Type[] types = fieldInformations.getParametreType();
			Type typeGeneric = Object.class;
			if(types != null && types.length > 0)
				typeGeneric = types[0];
			fakeChamp = new FakeChamp("V", typeGeneric, fieldInformations.getRelation(), fieldInformations.getAnnotations());
		}
		return fakeChamp;
	}
    public static ActionAbstrait<Collection> getInstance() {	
		return new ActionXmlCollectionType<>(Collection.class, null);
	}
	
	@Override
	public <U extends C> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return new ActionXmlCollectionType<>(type, (XmlUnmarshaller<?>) unmarshaller);
	}

	
	@SuppressWarnings("unchecked")	@Override
	protected <W> void integreObjet(String nomAttribut, W objet) {
		((Collection)obj).add(objet);
	}

	@Override
	protected void construitObjet() {
		//rien à faire
	}
	@Override
	protected FieldInformations getFieldInformationSpecialise(String nom) {
		return getFakeChamp();
	}
	
	@Override
	protected Class<?> getTypeAttribute(String nomAttribut) {
		return getFakeChamp().getValueType();
	}

}
