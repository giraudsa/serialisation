package giraudsa.marshall.deserialisation.binary.actions;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.ActionBinary;
import giraudsa.marshall.exception.NotImplementedSerializeException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

@SuppressWarnings("rawtypes")
public class ActionBinaryCollection<T extends Collection> extends ActionBinary<T> {

	public ActionBinaryCollection(Class<? extends T> type, TypeRelation relation, T objetPreConstruit, Unmarshaller<?> b) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, IOException, NotImplementedSerializeException {
		super(type, relation, objetPreConstruit, b);
	}

	@SuppressWarnings({ "unchecked", "unused" })
	@Override
	protected T readObject() throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException, ClassNotFoundException {
		if(!isDejaVu){
			for(int i = 0 ; i < readInt(); i++){
				obj.add(litObject(relation, Object.class));
			}
		}
		if(isDejaVu && TypeRelation.COMPOSITION == relation){
			for(Object value : obj){
				litObject(relation, Object.class);
			}
		}
		return obj;
	}


}
