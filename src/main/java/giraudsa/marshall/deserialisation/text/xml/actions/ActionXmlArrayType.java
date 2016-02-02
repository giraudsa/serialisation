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
	private FakeChamp fakeChamp;
	private List<Object> listeTampon = new ArrayList<Object>();
	private Class<?> componentType;
	private ActionXmlArrayType(Class<T> type, XmlUnmarshaller<?> unmarshaller) {
		super(type, unmarshaller);
		componentType = type.getComponentType();
	}
	private FakeChamp getFakeChamp(){
		if(fakeChamp == null){
			Class<?> type = fieldInformations.getValueType();
			Class<?> typeGeneric = TypeExtension.getTypeEnveloppe(type.getComponentType());
			fakeChamp = new FakeChamp("V", typeGeneric, fieldInformations.getRelation());
		}
		return fakeChamp;
	}
    public static ActionAbstrait<Object> getInstance() {	
		return new ActionXmlArrayType<Object>(Object.class, null);
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public <U extends T> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return new ActionXmlArrayType<U>(type, (XmlUnmarshaller<?>) unmarshaller);
	}

	
	@Override
	protected <W> void integreObjet(String nomAttribut, W objet) {
		listeTampon.add(objet);
	}

	@Override
	protected void construitObjet() {
		obj = Array.newInstance(componentType, listeTampon.size());
		for(int i = 0; i < listeTampon.size(); ++i){
			Object o = listeTampon.get(i);
			Array.set(obj, i, o);
		}
	}
	@Override
	protected FieldInformations getFieldInformationSpecialise(String nom) {
		return getFakeChamp();
	}
	
	@Override
	protected Class<?> getTypeAttribute(String nomAttribut) {
		return getFakeChamp().getValueType();
	}
}
