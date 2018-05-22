package giraudsa.marshall.deserialisation.text.xml.actions;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.xml.XmlUnmarshaller;
import utils.TypeExtension;
import utils.champ.FakeChamp;
import utils.champ.FieldInformations;

public class ActionXmlArrayType<T> extends ActionXmlComplexeObject<T> {
	public static ActionAbstrait<Object> getInstance() {
		return new ActionXmlArrayType<>(Object.class, null);
	}

	private final Class<?> componentType;
	private FakeChamp fakeChamp;
	private final List<Object> listeTampon = new ArrayList<>();

	private ActionXmlArrayType(final Class<T> type, final XmlUnmarshaller<?> unmarshaller) {
		super(type, unmarshaller);
		componentType = type.getComponentType();
	}

	@Override
	protected void construitObjet() {
		obj = Array.newInstance(componentType, listeTampon.size());
		for (int i = 0; i < listeTampon.size(); ++i) {
			final Object o = listeTampon.get(i);
			Array.set(obj, i, o);
		}
	}

	private FakeChamp getFakeChamp() {
		if (fakeChamp == null) {
			final Class<?> type = fieldInformations.getValueType();
			final Class<?> typeGeneric = TypeExtension.getTypeEnveloppe(type.getComponentType());
			fakeChamp = new FakeChamp("V", typeGeneric, fieldInformations.getRelation(),
					fieldInformations.getAnnotations());
		}
		return fakeChamp;
	}

	@Override
	protected FieldInformations getFieldInformationSpecialise(final String nom) {
		return getFakeChamp();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public <U extends T> ActionAbstrait<U> getNewInstance(final Class<U> type, final Unmarshaller unmarshaller) {
		return new ActionXmlArrayType<>(type, (XmlUnmarshaller<?>) unmarshaller);
	}

	@Override
	protected Class<?> getTypeAttribute(final String nomAttribut) {
		return getFakeChamp().getValueType();
	}

	@Override
	protected <W> void integreObjet(final String nomAttribut, final W objet) {
		listeTampon.add(objet);
	}
}
