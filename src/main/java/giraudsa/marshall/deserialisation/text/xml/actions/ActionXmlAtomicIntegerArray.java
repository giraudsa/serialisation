package giraudsa.marshall.deserialisation.text.xml.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicIntegerArray;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.xml.XmlUnmarshaller;
import utils.champ.FakeChamp;
import utils.champ.FieldInformations;

public class ActionXmlAtomicIntegerArray extends ActionXmlComplexeObject<AtomicIntegerArray> {
	public static ActionAbstrait<AtomicIntegerArray> getInstance() {
		return new ActionXmlAtomicIntegerArray(AtomicIntegerArray.class, null);
	}

	private final FakeChamp fakeChamp = new FakeChamp("V", Integer.class, TypeRelation.COMPOSITION, null);
	private final List<Integer> listeTampon = new ArrayList<>();

	private ActionXmlAtomicIntegerArray(final Class<AtomicIntegerArray> type, final XmlUnmarshaller<?> unmarshaller) {
		super(type, unmarshaller);
	}

	@Override
	protected void construitObjet() {
		obj = new AtomicIntegerArray(listeTampon.size());
		for (int i = 0; i < listeTampon.size(); ++i)
			((AtomicIntegerArray) obj).set(i, listeTampon.get(i));
	}

	@Override
	protected FieldInformations getFieldInformationSpecialise(final String nom) {
		return fakeChamp;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <U extends AtomicIntegerArray> ActionAbstrait<U> getNewInstance(final Class<U> type,
			final Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionXmlAtomicIntegerArray(AtomicIntegerArray.class,
				(XmlUnmarshaller<?>) unmarshaller);
	}

	@Override
	protected Class<?> getTypeAttribute(final String nomAttribut) {
		return Integer.class;
	}

	@Override
	protected <W> void integreObjet(final String nomAttribut, final W objet) {
		listeTampon.add((Integer) objet);
	}
}
