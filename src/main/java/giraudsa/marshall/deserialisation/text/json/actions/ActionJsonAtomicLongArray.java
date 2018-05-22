package giraudsa.marshall.deserialisation.text.json.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLongArray;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.json.JsonUnmarshaller;
import utils.champ.FakeChamp;
import utils.champ.FieldInformations;

public class ActionJsonAtomicLongArray extends ActionJsonComplexeObject<AtomicLongArray> {
	public static ActionAbstrait<?> getInstance() {
		return new ActionJsonAtomicLongArray(AtomicLongArray.class, null);
	}

	private final FakeChamp fakeChamp = new FakeChamp(null, Long.class, TypeRelation.COMPOSITION, null);
	private final List<Long> listeTampon = new ArrayList<>();

	private ActionJsonAtomicLongArray(final Class<AtomicLongArray> type, final JsonUnmarshaller<?> unmarshaller) {
		super(type, unmarshaller);
	}

	@Override
	protected void construitObjet() {
		obj = new AtomicLongArray(listeTampon.size());
		for (int i = 0; i < listeTampon.size(); ++i)
			((AtomicLongArray) obj).set(i, listeTampon.get(i));
	}

	@Override
	protected FieldInformations getFieldInformationSpecialise(final String nom) {
		return fakeChamp;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <U extends AtomicLongArray> ActionAbstrait<U> getNewInstance(final Class<U> type,
			final Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionJsonAtomicLongArray(AtomicLongArray.class,
				(JsonUnmarshaller<?>) unmarshaller);
	}

	@Override
	protected Class<?> getTypeAttribute(final String nomAttribut) {
		return Long.class;
	}

	@Override
	protected <W> void integreObjet(final String nomAttribut, final W objet) {
		listeTampon.add((Long) objet);
	}
}
