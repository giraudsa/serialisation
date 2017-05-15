package utils.champ;

import giraudsa.marshall.annotations.MarshallAsAttribute;
import giraudsa.marshall.annotations.Relation;
import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.SetValueException;
import utils.EntityManager;
import utils.TypeExtension;
import utils.generic.TypeToken;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.UUID;

public class Champ implements Comparable<Champ>, FieldInformations {
	private static final Annotation[] noAnnotation = new Annotation[0];
	protected String name;
	private final Field info;

	private final boolean isSimple;
	private TypeRelation relation;
	protected Class<?> valueType;
	protected TypeToken<?> typeToken;
	private String comparaison;
	private final boolean isChampId;

	Champ(Field info, boolean isSimple, boolean isChampId) {
		this.info = info;
		this.isSimple = isSimple;
		this.isChampId = isChampId;
		if (info != null) {
			typeToken = TypeToken.get(info.getGenericType());
			valueType = info.getType();
			MarshallAsAttribute metadata = info.getAnnotation(MarshallAsAttribute.class);
			name = metadata !=null ? metadata.name() : info.getName();
			Relation maRelation = info.getAnnotation(Relation.class);
			if (isSimple)
				relation = TypeRelation.COMPOSITION;
			else
				relation = maRelation != null ? maRelation.type() : TypeRelation.ASSOCIATION;
			
		}
	}
	
	private String getComparaison(){
		if(comparaison == null){
			StringBuilder sb = new StringBuilder();
			if (name.equals(ChampUid.UID_FIELD_NAME)){
				sb.append("0");
			}
			sb.append(name);
			sb.append(info.getDeclaringClass().getName());
			comparaison = sb.toString();
		}
		return comparaison;
	}

	@Override
	public int compareTo(Champ other) {
		int res = -1;
		if (isSimple == other.isSimple){
			res = getComparaison().compareTo(other.getComparaison());
		}else if (!isSimple && other.isSimple)
			res = 1;
		return res;
	}
	
	@Override
	public boolean equals(Object other){
		if(other instanceof Champ){
			return compareTo((Champ)other) == 0;
		}
		return false;
	}
	
	@Override public int hashCode() {
		String that = name + info.getDeclaringClass().getName();
		return that.hashCode();
	}

	@Override
	public void set(Object obj, Object value, Map<Object, UUID> dicoObjToFakeId) throws SetValueException{
		try {
			if(obj != null)
				if(name.equals(ChampUid.UID_FIELD_NAME) && info.get(obj) != null) //l'id est deja setté
					return;
				info.set(obj, value);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new SetValueException("impossible de setter " + value.toString() + " de type " + value.getClass().getName() + " dans le champ " + name + " de la classe " + info.getDeclaringClass(), e);
		}
	}
	
	public boolean isFakeId(){
		return info == null;
	}

	@Override
	public Object get(Object obj, Map<Object, UUID> dicoObjToFakeId, EntityManager entity) throws IllegalAccessException {
			return info.get(obj);
	}

	@Override
	public TypeRelation getRelation() {
		return relation;
	}

	public Field getInfo() {
		return info;
	}
	
	@Override
	public boolean isSimple(){
		return isSimple;
	}
	@Override
	public Class<?> getValueType(){
		return valueType;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isTypeDevinable(Object value) {
		Class<?> type = value.getClass();
		return TypeExtension.getTypeEnveloppe(this.valueType) == TypeExtension.getTypeEnveloppe(type);
	}
	
	@Override
	public Type[] getParametreType(){
		if(typeToken == null) 
			return new Type[0];
		Type type = typeToken.getType();
		if(type instanceof ParameterizedType){
			return ((ParameterizedType)type).getActualTypeArguments();
		}
		return new Type[0];
	}

	@Override
	public boolean isChampId() {
		return isChampId;
	}

	@Override
	public Object get(Object o) throws IllegalAccessException {
		return get(o, null, null);
	}
	
	@Override
	public Annotation[] getAnnotations(){
		if(info == null)
			return noAnnotation;
		return info.getAnnotations();
	}
	
	@Override
	public <T extends Annotation> T getAnnotation(Class<T> annotationClass){
		if(info == null)
			return null;
		return info.getAnnotation(annotationClass);
	}
	
}
