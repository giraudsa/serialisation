package giraudsa.marshall.deserialisation.text.xml.actions;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.xml.XmlUnmarshaller;
import utils.champ.FakeChamp;
import utils.champ.FieldInformations;

public class ActionXmlBitSet extends ActionXmlComplexeObject<BitSet> {
	public static ActionAbstrait<BitSet> getInstance() {
		return new ActionXmlBitSet(BitSet.class, null);
	}

	private final FakeChamp fakeChamp = new FakeChamp("V", Boolean.class, TypeRelation.COMPOSITION, null);
	private final List<Boolean> listeTampon = new ArrayList<>();

	private ActionXmlBitSet(final Class<BitSet> type, final XmlUnmarshaller<?> unmarshaller) {
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
		return (ActionAbstrait<U>) new ActionXmlBitSet(BitSet.class, (XmlUnmarshaller<?>) unmarshaller);
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
