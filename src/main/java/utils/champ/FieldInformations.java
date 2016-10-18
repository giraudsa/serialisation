package utils.champ;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.UUID;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.SetValueException;

public interface FieldInformations {
	public String getName();
	public TypeRelation getRelation();
	public Object get(Object o, Map<Object, UUID> dicoObjToFakeId) throws IllegalAccessException;
	public Object get(Object o) throws IllegalAccessException;
	public boolean isTypeDevinable(Object o);
	public Type[] getParametreType();
	public Class<?> getValueType();
	public boolean isChampId();
	public Annotation[] getAnnotations();
	public <T extends Annotation> T getAnnotation(Class<T> annotationClass);
	public void set(Object obj, Object value, Map<Object, UUID> dicoObjToFakeId) throws SetValueException;
	public boolean isSimple();
}
