package io.github.giraudsa.fidelis.deserialisation.text.json.actions;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import io.github.giraudsa.fidelis.annotations.TypeRelation;
import io.github.giraudsa.fidelis.deserialisation.ActionAbstrait;
import io.github.giraudsa.fidelis.deserialisation.Unmarshaller;
import io.github.giraudsa.fidelis.deserialisation.text.json.JsonUnmarshaller;
import io.github.giraudsa.fidelis.utils.champ.FakeChamp;
import io.github.giraudsa.fidelis.utils.champ.FieldInformations;

public class ActionJsonBitSet extends ActionJsonComplexeObject<BitSet> {
	public static ActionAbstrait<?> getInstance() {
		return new ActionJsonBitSet(BitSet.class, null);
	}

	private final FakeChamp fakeChamp = new FakeChamp(null, Boolean.class, TypeRelation.COMPOSITION, null);
	private final List<Boolean> listeTampon = new ArrayList<>();

	private ActionJsonBitSet(final Class<BitSet> type, final JsonUnmarshaller<?> unmarshaller) {
		super(type, unmarshaller);
	}

	@Override
	protected void construitObjet() {
		obj = new BitSet(listeTampon.size());
		for (int i = 0; i < listeTampon.size(); ++i)
			((BitSet) obj).set(i, listeTampon.get(i));
	}

	@Override
	protected FieldInformations getFieldInformationSpecialise(final String nom) {
		return fakeChamp;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <U extends BitSet> ActionAbstrait<U> getNewInstance(final Class<U> type, final Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionJsonBitSet(BitSet.class, (JsonUnmarshaller<?>) unmarshaller);
	}

	@Override
	protected Class<?> getTypeAttribute(final String nomAttribut) {
		return Boolean.class;
	}

	@Override
	protected <W> void integreObjet(final String nomAttribut, final W objet) {
		listeTampon.add((Boolean) objet);
	}
}
