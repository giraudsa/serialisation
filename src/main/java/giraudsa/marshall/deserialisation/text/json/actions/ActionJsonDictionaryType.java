package giraudsa.marshall.deserialisation.text.json.actions;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.json.ActionJson;
import giraudsa.marshall.deserialisation.text.json.JsonUnmarshaller;
import utils.Constants;
import utils.champ.FakeChamp;
import utils.champ.FieldInformations;

@SuppressWarnings("rawtypes")
public class ActionJsonDictionaryType<T extends Map> extends ActionJson<T> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionJsonDictionaryType.class);

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
				LOGGER.error("instanciation impossible pour " + type.getName(), e);
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
		if (Constants.VALEUR.equals(nomAttribut))
			return ArrayList.class;
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
			for (final Object o : (ArrayList<?>) objet)
				if (clefTampon == null)
					clefTampon = o;
				else {
					((Map) obj).put(clefTampon, o);
					clefTampon = null;
				}
	}

	@Override
	protected void rempliData(final String donnees) {
		// l'objet est construit à l'instanciation de la classe.
	}

}
