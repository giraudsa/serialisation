package giraudsa.marshall.deserialisation.text.xml.actions;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.xml.XmlUnmarshaller;
import utils.champ.FakeChamp;
import utils.champ.FieldInformations;

@SuppressWarnings("rawtypes")
public class ActionXmlDictionaryType<T extends Map> extends ActionXmlComplexeObject<T> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ActionXmlDictionaryType.class);

	public static ActionAbstrait<Map> getInstance() {
		return new ActionXmlDictionaryType<>(Map.class, null);
	}

	private FakeChamp fakeChampKey;
	private FakeChamp fakeChampValue;
	private Object keyTampon;

	private ActionXmlDictionaryType(final Class<T> type, final XmlUnmarshaller<?> xmlUnmarshaller) {
		super(type, xmlUnmarshaller);
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
		// rien a faire
	}

	private FakeChamp getFakeChamp() {
		if (keyTampon == null) {
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
	protected FieldInformations getFieldInformationSpecialise(final String nom) {
		return getFakeChamp();
	}

	@Override
	public <U extends T> ActionAbstrait<U> getNewInstance(final Class<U> type, final Unmarshaller unmarshaller) {
		return new ActionXmlDictionaryType<>(type, (XmlUnmarshaller<?>) unmarshaller);
	}

	@Override
	protected Class<?> getTypeAttribute(final String nomAttribut) {
		return getFakeChamp().getValueType();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected <W> void integreObjet(final String nomAttribut, final W objet) {
		if (keyTampon == null)
			keyTampon = objet;
		else {
			((Map) obj).put(keyTampon, objet);
			keyTampon = null;
		}
	}
}
