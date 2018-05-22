package utils.champ;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.UUID;

import giraudsa.marshall.annotations.MarshallAsAttribute;
import giraudsa.marshall.annotations.Relation;
import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.SetValueException;
import utils.EntityManager;
import utils.TypeExtension;
import utils.generic.TypeToken;

public class Champ implements Comparable<Champ>, FieldInformations {
	private static final Annotation[] noAnnotation = new Annotation[0];
	private String comparaison;
	private final Field info;

	private final boolean isChampId;
	private final boolean isSimple;
	protected String name;
	private TypeRelation relation;
	protected TypeToken<?> typeToken;
	protected Class<?> valueType;

	Champ(final Field info, final boolean isSimple, final boolean isChampId) {
		this.info = info;
		this.isSimple = isSimple;
		this.isChampId = isChampId;
		if (info != null) {
			typeToken = TypeToken.get(info.getGenericType());
			valueType = info.getType();
			final MarshallAsAttribute metadata = info.getAnnotation(MarshallAsAttribute.class);
			name = metadata != null ? metadata.name() : info.getName();
			final Relation maRelation = info.getAnnotation(Relation.class);
			if (isSimple)
				relation = TypeRelation.COMPOSITION;
			else
				relation = maRelation != null ? maRelation.type() : TypeRelation.ASSOCIATION;

		}
	}

	@Override
	public int compareTo(final Champ other) {
		int res = -1;
		if (isSimple == other.isSimple)
			res = getComparaison().compareTo(other.getComparaison());
		else if (!isSimple && other.isSimple)
			res = 1;
		return res;
	}

	@Override
	public boolean equals(final Object other) {
		if (other instanceof Champ)
			return compareTo((Champ) other) == 0;
		return false;
	}

	@Override
	public Object get(final Object o) throws IllegalAccessException {
		return get(o, null, null);
	}

	@Override
	public Object get(final Object obj, final Map<Object, UUID> dicoObjToFakeId, final EntityManager entity)
			throws IllegalAccessException {
		return info.get(obj);
	}

	@Override
	public <T extends Annotation> T getAnnotation(final Class<T> annotationClass) {
		if (info == null)
			return null;
		return info.getAnnotation(annotationClass);
	}

	@Override
	public Annotation[] getAnnotations() {
		if (info == null)
			return noAnnotation;
		return info.getAnnotations();
	}

	private String getComparaison() {
		if (comparaison == null) {
			final StringBuilder sb = new StringBuilder();
			if (name.equals(ChampUid.UID_FIELD_NAME))
				sb.append("0");
			sb.append(name);
			sb.append(info.getDeclaringClass().getName());
			comparaison = sb.toString();
		}
		return comparaison;
	}

	public Field getInfo() {
		return info;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Type[] getParametreType() {
		if (typeToken == null)
			return new Type[0];
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
		return valueType;
	}

	@Override
	public int hashCode() {
		final String that = name + info.getDeclaringClass().getName();
		return that.hashCode();
	}

	@Override
	public boolean isChampId() {
		return isChampId;
	}

	public boolean isFakeId() {
		return info == null;
	}

	@Override
	public boolean isSimple() {
		return isSimple;
	}

	@Override
	public boolean isTypeDevinable(final Object value) {
		final Class<?> type = value.getClass();
		return TypeExtension.getTypeEnveloppe(valueType) == TypeExtension.getTypeEnveloppe(type);
	}

	@Override
	public void set(final Object obj, final Object value, final Map<Object, UUID> dicoObjToFakeId)
			throws SetValueException {
		try {
			if (obj != null)
				if (name.equals(ChampUid.UID_FIELD_NAME))
					setChampId(obj, value);
				else
					info.set(obj, value);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new SetValueException(
					"impossible de setter " + value.toString() + " de type " + value.getClass().getName()
							+ " dans le champ " + name + " de la classe " + info.getDeclaringClass(),
					e);
		}
	}

	private void setChampId(final Object obj, final Object value)
			throws IllegalArgumentException, IllegalAccessException {
		if (info.get(obj) == null)
			info.set(obj, value);
		else if ("0".equals(info.get(obj).toString()))
			info.set(obj, value);
	}

}
