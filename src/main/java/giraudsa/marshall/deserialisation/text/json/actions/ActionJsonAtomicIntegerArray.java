package giraudsa.marshall.deserialisation.text.json.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicIntegerArray;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.json.JsonUnmarshaller;
import utils.champ.FakeChamp;
import utils.champ.FieldInformations;

public class ActionJsonAtomicIntegerArray extends ActionJsonComplexeObject<AtomicIntegerArray> {
	public static ActionAbstrait<?> getInstance() {
		return new ActionJsonAtomicIntegerArray(AtomicIntegerArray.class, null);
	}

	private final FakeChamp fakeChamp = new FakeChamp(null, Integer.class, TypeRelation.COMPOSITION, null);
	private final List<Integer> listeTampon = new ArrayList<>();

	private ActionJsonAtomicIntegerArray(final Class<AtomicIntegerArray> type, final JsonUnmarshaller<?> unmarshaller) {
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
		return (ActionAbstrait<U>) new ActionJsonAtomicIntegerArray(AtomicIntegerArray.class,
				(JsonUnmarshaller<?>) unmarshaller);
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
