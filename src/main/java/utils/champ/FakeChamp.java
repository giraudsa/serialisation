package utils.champ;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.UUID;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.SetValueException;
import utils.EntityManager;
import utils.TypeExtension;
import utils.generic.TypeToken;

public class FakeChamp implements FieldInformations {
	private static final Annotation[] noAnnotation = new Annotation[0];
	private final Annotation[] annotations;
	private final boolean isSimple;
	private final String name;
	private final TypeRelation relation;
	private final TypeToken<?> typeToken;

	public FakeChamp(final String name, final Type type, final TypeRelation relation, final Annotation[] annotations) {
		super();
		this.name = name;
		typeToken = TypeToken.get(type);
		this.relation = relation;
		isSimple = TypeExtension.isSimple(typeToken.getRawType());
		this.annotations = annotations == null ? noAnnotation : annotations;
	}

	@Override
	public Object get(final Object o) throws IllegalAccessException {
		return get(o, null, null);
	}

	@Override
	public Object get(final Object o, final Map<Object, UUID> dicoObjToFakeId, final EntityManager entity)
			throws IllegalAccessException {
		return o;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Annotation> T getAnnotation(final Class<T> annotationClass) {
		for (final Annotation annotation : getAnnotations())
			if (annotationClass.isInstance(annotation))
				return (T) annotation;
		return null;
	}

	@Override
	public Annotation[] getAnnotations() {
		return annotations;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Type[] getParametreType() {
		final Type type = typeToken.getType();
		if (type instanceof ParameterizedType)
			return ((ParameterizedType) type).getActualTypeArguments();
		return new Type[0];
	}

	@Override
	public TypeRelation getRelation() {
		return relation;
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
	public boolean isSimple() {
		return false;
	}

	@Override
	public boolean isTypeDevinable(final Object o) {
		final Class<?> valueType = o.getClass();
		return isSimple || typeToken.getRawType() == valueType;
	}

	@Override
	public void set(final Object obj, final Object value, final Map<Object, UUID> dicoObjToFakeId)
			throws SetValueException {
		// rien à faire

	}

}
