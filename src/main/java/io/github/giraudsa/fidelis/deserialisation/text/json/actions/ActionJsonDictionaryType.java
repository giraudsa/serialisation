package io.github.giraudsa.fidelis.deserialisation.text.json.actions;

import java.lang.System.Logger.Level;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;


import io.github.giraudsa.fidelis.deserialisation.ActionAbstrait;
import io.github.giraudsa.fidelis.deserialisation.Unmarshaller;
import io.github.giraudsa.fidelis.deserialisation.text.json.ActionJson;
import io.github.giraudsa.fidelis.deserialisation.text.json.JsonUnmarshaller;
import io.github.giraudsa.fidelis.utils.Constants;
import io.github.giraudsa.fidelis.utils.champ.FakeChamp;
import io.github.giraudsa.fidelis.utils.champ.FieldInformations;

@SuppressWarnings("rawtypes")
public class ActionJsonDictionaryType<T extends Map> extends ActionJson<T> {

	private static final System.Logger LOGGER = System.getLogger(ActionJsonDictionaryType.class.getName());

	public static ActionAbstrait<Map> getInstance() {
		return new ActionJsonDictionaryType<>(Map.class, null);
	}

	private Object clefTampon = null;
	private FakeChamp fakeChampKey;
	private FakeChamp fakeChampValue;

	private ActionJsonDictionaryType(final Class<T> type, final JsonUnmarshaller<?> jsonUnmarshaller) {
		super(type, jsonUnmarshaller);
		if (!type.isInterface())
			try {
				obj = type.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				obj = new HashMap<>();
				LOGGER.log(Level.ERROR, "instanciation impossible pour " + type.getName(), e);
			}
	}

	@Override
	protected void construitObjet() {
		// l'objet est construit à l'instanciation de la classe.
	}

	private FakeChamp getFakeChamp() {
		if (clefTampon == null) {
			if (fakeChampKey == null) {
				final Type[] types = fieldInformations.getParametreType();
				Type typeGeneric = Object.class;
				if (types != null && types.length > 0)
					typeGeneric = types[0];
				fakeChampKey = new FakeChamp("K", typeGeneric, fieldInformations.getRelation(),
						fieldInformations.getAnnotations());
			}
			return fakeChampKey;
		}
		if (fakeChampValue == null) {
			final Type[] types = fieldInformations.getParametreType();
			Type typeGeneric = Object.class;
			if (types != null && types.length > 1)
				typeGeneric = types[1];
			fakeChampValue = new FakeChamp("V", typeGeneric, fieldInformations.getRelation(),
					fieldInformations.getAnnotations());
		}
		return fakeChampValue;
	}

	@Override
	protected FieldInformations getFieldInformationSpecialise(final String nomAttribut) {
		if (Constants.VALEUR.equals(nomAttribut))
			return fieldInformations;
		return getFakeChamp();
	}

	@Override
	public <U extends T> ActionAbstrait<U> getNewInstance(final Class<U> type, final Unmarshaller unmarshaller) {
		return new ActionJsonDictionaryType<>(type, (JsonUnmarshaller<?>) unmarshaller);
	}

	@Override
	protected Class<?> getTypeAttribute(final String nomAttribut) {
		// le tableau "__valeur" alterne clefs et valeurs : il est lu par une action
		// dictionnaire (et non une liste qui typerait tout comme une clef).
		if (Constants.VALEUR.equals(nomAttribut))
			return type.isInterface() ? HashMap.class : type;
		return getFakeChamp().getValueType();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected <W> void integreObjet(final String nomAttribut, final W objet) {
		if (nomAttribut == null) {
			if (clefTampon == null)
				clefTampon = objet;
			else {
				((Map) obj).put(clefTampon, objet);
				clefTampon = null;
			}

		} else
			((Map) obj).putAll((Map) objet);
	}

	@Override
	protected void rempliData(final String donnees) {
		// l'objet est construit à l'instanciation de la classe.
	}

}
