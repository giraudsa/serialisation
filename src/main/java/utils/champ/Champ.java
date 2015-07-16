package utils.champ;

import java.lang.reflect.Field;

import com.actemium.marshall.annotations.MarshallAsAttribute;
import com.actemium.marshall.annotations.Relation;
import com.actemium.marshall.annotations.TypeRelation;

public class Champ implements Comparable<Champ> {

	public String name;
	public Field info;

	public boolean isSimple;
	public TypeRelation relation;
	public Class<?> valueType;

	public int compareTo(Champ other) {
		int res = -1;
		if (isSimple == other.isSimple)
			res = name.compareTo(other.name);
		else if (!isSimple && other.isSimple)
			res = 1;
		return res;
	}

	public void set(Object obj, Object value) throws IllegalArgumentException, IllegalAccessException {
		
		info.set(obj, value);
	}
	
	public boolean isFakeId(){
		return info == null;
	}

	public Object get(Object obj) throws IllegalArgumentException, IllegalAccessException {
			return info.get(obj);
	}

	Champ(Field info, Boolean isSimple) {
		this.info = info;
		this.isSimple = isSimple;
		if (info != null) {
			valueType = info.getType();
			MarshallAsAttribute metadata = info.getAnnotation(MarshallAsAttribute.class);
			name = metadata !=null ? metadata.name() : info.getName();
			Relation maRelation = info.getAnnotation(Relation.class);
			relation = maRelation != null ? maRelation.type() : TypeRelation.ASSOCIATION;
		}
	}
	
}
