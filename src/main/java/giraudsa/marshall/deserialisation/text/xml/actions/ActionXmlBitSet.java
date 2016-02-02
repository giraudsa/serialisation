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
	private FakeChamp fakeChamp = new FakeChamp("V", Boolean.class, TypeRelation.COMPOSITION);
	private List<Boolean> listeTampon = new ArrayList<Boolean>();
	private ActionXmlBitSet(Class<BitSet> type, XmlUnmarshaller<?> unmarshaller) {
		super(type, unmarshaller);
	}
    public static ActionAbstrait<BitSet> getInstance() {	
		return new ActionXmlBitSet(BitSet.class, null);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <U extends BitSet> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionXmlBitSet(BitSet.class, (XmlUnmarshaller<?>) unmarshaller);
	}

	
	@Override
	protected <W> void integreObjet(String nomAttribut, W objet) {
		listeTampon.add((Boolean)objet);
	}

	@Override
	protected void construitObjet() {
		obj = new BitSet(listeTampon.size());
		for(int i = 0; i < listeTampon.size(); ++i){
			((BitSet)obj).set(i, listeTampon.get(i));
		}
	}
	@Override
	protected FieldInformations getFieldInformationSpecialise(String nom) {
		return fakeChamp;
	}
	
	@Override
	protected Class<?> getTypeAttribute(String nomAttribut) {
		return Boolean.class;
	}
}
