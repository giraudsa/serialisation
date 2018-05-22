package giraudsa.marshall.deserialisation.text.json.actions;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.json.JsonUnmarshaller;
import utils.Constants;
import utils.champ.FakeChamp;
import utils.champ.FieldInformations;

@SuppressWarnings("rawtypes")
public class ActionJsonCollectionType<T extends Collection> extends ActionJsonComplexeObject<T> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionJsonCollectionType.class);

	public static ActionAbstrait<Collection> getInstance() {
		return new ActionJsonCollectionType<>(Collection.class, null);
	}

	private FakeChamp fakeChamp;

	private ActionJsonCollectionType(final Class<T> type, final JsonUnmarshaller<?> jsonUnmarshaller) {
		super(type, jsonUnmarshaller);
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
		// rien a faire
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
	protected FieldInformations getFieldInformationSpecialise(final String nomAttribut) {
		if (Constants.VALEUR.equals(nomAttribut))
			return fieldInformations;
		return getFakeChamp();
	}

	@Override
	public <U extends T> ActionAbstrait<U> getNewInstance(final Class<U> type, final Unmarshaller unmarshaller) {
		return new ActionJsonCollectionType<>(type, (JsonUnmarshaller<?>) unmarshaller);
	}

	@Override
	protected Class<?> getTypeAttribute(final String nomAttribut) {
		if (Constants.VALEUR.equals(nomAttribut))
			return ArrayList.class;
		return getFakeChamp().getValueType();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected <W> void integreObjet(final String nomAttribut, final W objet) {
		if (nomAttribut == null)
			((Collection) obj).add(objet);
		else
			for (final Object o : (ArrayList) objet)
				((Collection) obj).add(o);
	}

}
