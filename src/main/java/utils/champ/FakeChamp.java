package utils.champ;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.UUID;

import giraudsa.marshall.annotations.TypeRelation;
import utils.TypeExtension;
import utils.generic.TypeToken;

public class FakeChamp implements FieldInformations {

	private String name;
	TypeToken<?> typeToken;
	private TypeRelation relation;
	private boolean isSimple;
	
	
	public FakeChamp(String name, Type type, TypeRelation relation) {
		super();
		this.name = name;
		this.typeToken = TypeToken.get(type);
		this.relation = relation;
		isSimple = TypeExtension.isSimple(typeToken.getRawType());
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public TypeRelation getRelation() {
		return relation;
	}

	@Override
	public Object get(Object o, Map<Object, UUID> dicoObjToFakeId) throws IllegalAccessException {
		return o;
	}

	@Override
	public boolean isTypeDevinable(Object o) {
		Class<?> valueType = o.getClass();
		return isSimple || typeToken.getRawType() == valueType;
	}
	
	@Override
	public Type[] getParametreType(){
		Type type = typeToken.getType();
		if(type instanceof ParameterizedType){
			return ((ParameterizedType)type).getActualTypeArguments();
		}
		return new Type[0];
	}

	@Override
	public Class<?> getValueType() {
		return typeToken.getRawType();
	}

	@Override
	public boolean isChampId() {
		return false;
	}

	@Override
	public Object get(Object o) throws IllegalAccessException {
		return get(o, null);
	}

}
