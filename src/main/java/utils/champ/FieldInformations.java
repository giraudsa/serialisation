package utils.champ;

import java.lang.reflect.Type;

import giraudsa.marshall.annotations.TypeRelation;

public interface FieldInformations {
	public String getName();
	public TypeRelation getRelation();
	public Object get(Object o) throws IllegalAccessException;
	public boolean isTypeDevinable(Object o);
	public Type[] getParametreType();
	public Class<?> getValueType();
}
