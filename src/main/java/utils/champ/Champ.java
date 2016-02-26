package utils.champ;

import giraudsa.marshall.annotations.MarshallAsAttribute;
import giraudsa.marshall.annotations.Relation;
import giraudsa.marshall.annotations.TypeRelation;
import utils.TypeExtension;
import utils.generic.TypeToken;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class Champ implements Comparable<Champ>, FieldInformations {

	protected String name;
	private final Field info;

	private final boolean isSimple;
	private TypeRelation relation;
	protected Class<?> valueType;
	protected TypeToken<?> typeToken;
	private String comparaison;

	Champ(Field info, Boolean isSimple) {
		this.info = info;
		this.isSimple = isSimple;
		if (info != null) {
			typeToken = TypeToken.get(info.getGenericType());
			valueType = info.getType();
			MarshallAsAttribute metadata = info.getAnnotation(MarshallAsAttribute.class);
			name = metadata !=null ? metadata.name() : info.getName();
			Relation maRelation = info.getAnnotation(Relation.class);
			relation = maRelation != null ? maRelation.type() : TypeRelation.ASSOCIATION;
			if (isSimple)
				relation = TypeRelation.COMPOSITION;
		}
	}
	
	protected String getComparaison(){
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

	public void set(Object obj, Object value){
		try {
			if(obj != null && !Modifier.isFinal(info.getModifiers()))
				info.set(obj, value);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean isFakeId(){
		return info == null;
	}

	public Object get(Object obj) throws IllegalAccessException {
			return info.get(obj);
	}

	public TypeRelation getRelation() {
		return relation;
	}

	public Field getInfo() {
		return info;
	}
	
	public boolean isSimple(){
		return isSimple;
	}
	public Class<?> getValueType(){
		return valueType;
	}

	public String getName() {
		return name;
	}

	public boolean isTypeDevinable(Object value) {
		Class<?> type = value.getClass();
		return TypeExtension.getTypeEnveloppe(this.valueType) == TypeExtension.getTypeEnveloppe(type);
	}
	
	public Type[] getParametreType(){
		Type type = typeToken.getType();
		if(type instanceof ParameterizedType){
			return ((ParameterizedType)type).getActualTypeArguments();
		}
		return new Type[0];
	}
}
