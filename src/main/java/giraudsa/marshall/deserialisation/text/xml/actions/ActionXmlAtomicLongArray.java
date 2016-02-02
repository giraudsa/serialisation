package giraudsa.marshall.deserialisation.text.xml.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLongArray;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.xml.XmlUnmarshaller;
import utils.champ.FakeChamp;
import utils.champ.FieldInformations;

public class ActionXmlAtomicLongArray extends ActionXmlComplexeObject<AtomicLongArray> {
	private FakeChamp fakeChamp = new FakeChamp("V", Long.class, TypeRelation.COMPOSITION);
	private List<Long> listeTampon = new ArrayList<Long>();
	private ActionXmlAtomicLongArray(Class<AtomicLongArray> type, XmlUnmarshaller<?> unmarshaller) {
		super(type, unmarshaller);
	}
    public static ActionAbstrait<AtomicLongArray> getInstance() {	
		return new ActionXmlAtomicLongArray(AtomicLongArray.class, null);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <U extends AtomicLongArray> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionXmlAtomicLongArray(AtomicLongArray.class, (XmlUnmarshaller<?>) unmarshaller);
	}

	
	@Override
	protected <W> void integreObjet(String nomAttribut, W objet) {
		listeTampon.add((Long)objet);
	}

	@Override
	protected void construitObjet() {
		obj = new AtomicLongArray(listeTampon.size());
		for(int i = 0; i < listeTampon.size(); ++i){
			((AtomicLongArray)obj).set(i, listeTampon.get(i));
		}
	}
	@Override
	protected FieldInformations getFieldInformationSpecialise(String nom) {
		return fakeChamp;
	}
	
	@Override
	protected Class<?> getTypeAttribute(String nomAttribut) {
		return Long.class;
	}
}
