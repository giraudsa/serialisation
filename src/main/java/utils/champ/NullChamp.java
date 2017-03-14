package utils.champ;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.UUID;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.SetValueException;
import utils.EntityManager;

public class NullChamp implements FieldInformations {

	private static final Type[] TypeNull = new Type[0];
	private static final Annotation[] AnnotationNull = new Annotation[0];
	
	private static final NullChamp instance = new NullChamp();
	
	public static final NullChamp getInstance(){
		return instance;
	}
	
	@Override
	public String getName() {
		return "nullName";
	}

	@Override
	public TypeRelation getRelation() {
		return TypeRelation.ASSOCIATION;
	}

	@Override
	public Object get(Object o, Map<Object, UUID> dicoObjToFakeId, EntityManager entity) throws IllegalAccessException {
		return null;
	}

	@Override
	public Object get(Object o) throws IllegalAccessException {
		return null;
	}

	@Override
	public boolean isTypeDevinable(Object o) {
		return false;
	}

	@Override
	public Type[] getParametreType() {
		return TypeNull;
	}

	@Override
	public Class<?> getValueType() {
		return Object.class;
	}

	@Override
	public boolean isChampId() {
		return false;
	}

	@Override
	public Annotation[] getAnnotations() {
		return AnnotationNull;
	}

	@Override
	public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
		return null;
	}

	@Override
	public void set(Object obj, Object value, Map<Object, UUID> dicoObjToFakeId) throws SetValueException {
		// rien à faire
		
	}

	@Override
	public boolean isSimple() {
		return false;
	}

}
