package giraudsa.marshall.deserialisation.binary.actions;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.ActionBinary;
import giraudsa.marshall.exception.NotImplementedSerializeException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class ActionBinaryDictionary<T extends Map> extends ActionBinary<T> {

	public ActionBinaryDictionary(Class<? extends T> type, TypeRelation relation, T objetPreconstruit, Unmarshaller<?> b) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, IOException, NotImplementedSerializeException {
		super(type, relation, objetPreconstruit, b);
	}

	@SuppressWarnings({ "unchecked", "unused" })
	@Override
	protected T readObject() throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException, ClassNotFoundException {
		if(!isDejaVu){
			for(int i = 0 ; i < readInt(); i++){
				Object key = litObject(relation, Object.class);
				Object value = litObject(relation, Object.class);
				obj.put(key, value);
			}
		}
		if(isDejaVu && TypeRelation.COMPOSITION == relation){
			for(Object entry : obj.entrySet()){
				litObject(relation, Object.class);
				litObject(relation, Object.class);
			}
		}
		return obj;
	}
}