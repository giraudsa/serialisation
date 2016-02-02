package utils.champ;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

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

	public String getName() {
		return name;
	}

	public TypeRelation getRelation() {
		return relation;
	}

	public Object get(Object o) throws IllegalAccessException {
		return o;
	}

	public boolean isTypeDevinable(Object o) {
		Class<?> valueType = o.getClass();
		return isSimple || typeToken.getRawType() == valueType;
	}
	
	public Type[] getParametreType(){
		Type type = typeToken.getType();
		if(type instanceof ParameterizedType){
			return ((ParameterizedType)type).getActualTypeArguments();
		}
		return new Type[0];
	}

	public Class<?> getValueType() {
		return typeToken.getRawType();
	}

}
