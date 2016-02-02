package giraudsa.marshall.deserialisation.text.json.actions;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.json.JsonUnmarshaller;
import utils.champ.FakeChamp;
import utils.champ.FieldInformations;

public class ActionJsonArrayType<T> extends ActionJsonComplexeObject<T> {
	private FakeChamp fakeChamp;
	private List<Object> listeTampon = new ArrayList<Object>();
	private Class<?> componentType;
	private ActionJsonArrayType(Class<T> type, JsonUnmarshaller<?> unmarshaller) {
		super(type, unmarshaller);
		componentType = type.getComponentType();
	}
	private FakeChamp getFakeChamp(){
		if(fakeChamp == null){
			Class<?> type = fieldInformations.getValueType();
			Class<?> typeGeneric = type.getComponentType();
			fakeChamp = new FakeChamp("V", typeGeneric, fieldInformations.getRelation());
		}
		return fakeChamp;
	}
    public static ActionAbstrait<Object> getInstance() {	
		return new ActionJsonArrayType<Object>(Object.class, null);
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public <U extends T> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return new ActionJsonArrayType<U>(type, (JsonUnmarshaller<?>) unmarshaller);
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
