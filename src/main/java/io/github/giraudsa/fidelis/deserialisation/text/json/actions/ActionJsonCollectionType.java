package io.github.giraudsa.fidelis.deserialisation.text.json.actions;

import java.lang.System.Logger.Level;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;


import io.github.giraudsa.fidelis.deserialisation.ActionAbstrait;
import io.github.giraudsa.fidelis.deserialisation.Unmarshaller;
import io.github.giraudsa.fidelis.deserialisation.text.json.JsonUnmarshaller;
import io.github.giraudsa.fidelis.utils.Constants;
import io.github.giraudsa.fidelis.utils.champ.FakeChamp;
import io.github.giraudsa.fidelis.utils.champ.FieldInformations;
import io.github.giraudsa.fidelis.utils.TypeExtension;

@SuppressWarnings("rawtypes")
public class ActionJsonCollectionType<T extends Collection> extends ActionJsonComplexeObject<T> {

	private static final System.Logger LOGGER = System.getLogger(ActionJsonCollectionType.class.getName());

	public static ActionAbstrait<Collection> getInstance() {
		return new ActionJsonCollectionType<>(Collection.class, null);
	}

	private FakeChamp fakeChamp;

	private ActionJsonCollectionType(final Class<T> type, final JsonUnmarshaller<?> jsonUnmarshaller) {
		super(type, jsonUnmarshaller);
		Class<?> ttype = type;
		if (TypeExtension.isHibernate(type) || type.isInterface())
			ttype = ArrayList.class;
		try {
			obj = ttype.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			LOGGER.log(Level.DEBUG, "impossible de créer une instance de " + ttype.getName(), e);
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
