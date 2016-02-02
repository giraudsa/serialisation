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
	private FakeChamp fakeChamp = new FakeChamp("V", Integer.class, TypeRelation.COMPOSITION);
	private List<Integer> listeTampon = new ArrayList<Integer>();
	private ActionXmlAtomicIntegerArray(Class<AtomicIntegerArray> type, XmlUnmarshaller<?> unmarshaller) {
		super(type, unmarshaller);
	}
    public static ActionAbstrait<AtomicIntegerArray> getInstance() {	
		return new ActionXmlAtomicIntegerArray(AtomicIntegerArray.class, null);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <U extends AtomicIntegerArray> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionXmlAtomicIntegerArray(AtomicIntegerArray.class, (XmlUnmarshaller<?>) unmarshaller);
	}

	
	@Override
	protected <W> void integreObjet(String nomAttribut, W objet) {
		listeTampon.add((Integer)objet);
	}

	@Override
	protected void construitObjet() {
		obj = new AtomicIntegerArray(listeTampon.size());
		for(int i = 0; i < listeTampon.size(); ++i){
			((AtomicIntegerArray)obj).set(i, listeTampon.get(i));
		}
	}
	@Override
	protected FieldInformations getFieldInformationSpecialise(String nom) {
		return fakeChamp;
	}
	
	@Override
	protected Class<?> getTypeAttribute(String nomAttribut) {
		return Integer.class;
	}
}
