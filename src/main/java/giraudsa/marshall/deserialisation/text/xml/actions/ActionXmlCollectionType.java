package giraudsa.marshall.deserialisation.text.xml.actions;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.xml.XmlUnmarshaller;
import utils.champ.FakeChamp;
import utils.champ.FieldInformations;

@SuppressWarnings("rawtypes")
public class ActionXmlCollectionType<C extends Collection> extends ActionXmlComplexeObject<C> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ActionXmlCollectionType.class);

	public static ActionAbstrait<Collection> getInstance() {
		return new ActionXmlCollectionType<>(Collection.class, null);
	}

	private FakeChamp fakeChamp;

	private ActionXmlCollectionType(final Class<C> type, final XmlUnmarshaller<?> unmarshaller) {
		super(type, unmarshaller);
		Class<?> ttype = type;
		if (type.getName().toLowerCase().indexOf("hibernate") != -1 || type.isInterface())
			ttype = ArrayList.class;
		try {
			obj = ttype.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			LOGGER.debug("impossible de créer une instance de " + ttype.getName(), e);
			obj = new ArrayList<>();
		}
	}

	@Override
	protected void construitObjet() {
		// rien à faire
	}

	private FakeChamp getFakeChamp() {
		if (fakeChamp == null) {
			final Type[] types = fieldInformations.getParametreType();
			Type typeGeneric = Object.class;
			if (types != null && types.length > 0)
				typeGeneric = types[0];
			fakeChamp = new FakeChamp("V", typeGeneric, fieldInformations.getRelation(),
					fieldInformations.getAnnotations());
		}
		return fakeChamp;
	}

	@Override
	protected FieldInformations getFieldInformationSpecialise(final String nom) {
		return getFakeChamp();
	}

	@Override
	public <U extends C> ActionAbstrait<U> getNewInstance(final Class<U> type, final Unmarshaller unmarshaller) {
		return new ActionXmlCollectionType<>(type, (XmlUnmarshaller<?>) unmarshaller);
	}

	@Override
	protected Class<?> getTypeAttribute(final String nomAttribut) {
		return getFakeChamp().getValueType();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected <W> void integreObjet(final String nomAttribut, final W objet) {
		((Collection) obj).add(objet);
	}

}
