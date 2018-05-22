package utils.champ;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.UUID;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.SetValueException;
import utils.EntityManager;

public class NullChamp implements FieldInformations {

	private static final Annotation[] AnnotationNull = new Annotation[0];
	private static final NullChamp instance = new NullChamp();

	private static final Type[] TypeNull = new Type[0];

	public static final NullChamp getInstance() {
		return instance;
	}

	@Override
	public Object get(final Object o) throws IllegalAccessException {
		return null;
	}

	@Override
	public Object get(final Object o, final Map<Object, UUID> dicoObjToFakeId, final EntityManager entity)
			throws IllegalAccessException {
		return null;
	}

	@Override
	public <T extends Annotation> T getAnnotation(final Class<T> annotationClass) {
		return null;
	}

	@Override
	public Annotation[] getAnnotations() {
		return AnnotationNull;
	}

	@Override
	public String getName() {
		return "nullName";
	}

	@Override
	public Type[] getParametreType() {
		return TypeNull;
	}

	@Override
	public TypeRelation getRelation() {
		return TypeRelation.ASSOCIATION;
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
	public boolean isSimple() {
		return false;
	}

	@Override
	public boolean isTypeDevinable(final Object o) {
		return false;
	}

	@Override
	public void set(final Object obj, final Object value, final Map<Object, UUID> dicoObjToFakeId)
			throws SetValueException {
		// rien à faire

	}

}
