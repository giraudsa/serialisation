package giraudsa.marshall.serialisation.binary.actions;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.binary.ActionBinary;
import giraudsa.marshall.serialisation.binary.BinaryMarshaller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Map.Entry;

@SuppressWarnings("rawtypes")
public class ActionBinaryDictionaryType<T extends Map> extends ActionBinary<T> {

	public ActionBinaryDictionaryType(Class<? super T> type, T obj, TypeRelation relation, Boolean isDejaVu, BinaryMarshaller b) throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException {
		super(type,obj, relation, isDejaVu, b);
	}
	
	@Override
	public void marshall(T obj, TypeRelation relation) throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
			SecurityException, NotImplementedSerializeException {
		if(!isDejaVu){
			writeInt(obj.size());
			for (Object entry : obj.entrySet()) {
				Object key = ((Entry)entry).getKey();
				Object value = ((Entry)entry).getValue();
				traiteObject(key, relation, true);
				traiteObject(value, relation, true);
			}
		}else if(relation == TypeRelation.COMPOSITION){//deja vu, donc on passe ici qd la relation est de type COMPOSITION
			for(Object entry : obj.entrySet()){
				traiteObject(((Entry)entry).getKey(), relation, true);
				traiteObject(((Entry)entry).getValue(), relation, true);
			}
		}
	}
}
