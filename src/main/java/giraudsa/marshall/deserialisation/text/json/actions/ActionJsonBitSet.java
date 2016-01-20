package giraudsa.marshall.deserialisation.text.json.actions;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.json.JsonUnmarshaller;
import utils.champ.FakeChamp;
import utils.champ.FieldInformations;

public class ActionJsonBitSet extends ActionJsonComplexeObject<BitSet> {
	private FakeChamp fakeChamp = new FakeChamp(null, Boolean.class, TypeRelation.COMPOSITION);
	private List<Boolean> listeTampon = new ArrayList<>();
	private ActionJsonBitSet(Class<BitSet> type, JsonUnmarshaller<?> unmarshaller) {
		super(type, unmarshaller);
	}
	
    public static ActionAbstrait<?> getInstance() {	
		return new ActionJsonBitSet(BitSet.class, null);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <U extends BitSet> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionJsonBitSet(BitSet.class, (JsonUnmarshaller<?>) unmarshaller);
	}

	
	@Override
	protected <W> void integreObjet(String nomAttribut, W objet) {
		listeTampon.add((Boolean) objet);
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
