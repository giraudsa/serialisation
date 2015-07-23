package giraudsa.marshall.serialisation.binary.actions;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.binary.ActionBinary;
import giraudsa.marshall.serialisation.binary.BinaryMarshaller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

@SuppressWarnings("rawtypes")
public class ActionBinaryCollectionType<T extends Collection> extends ActionBinary<T> {

	public ActionBinaryCollectionType(Class<? super T> type, Object obj, TypeRelation relation, Boolean isDejaVu, BinaryMarshaller b) throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException {
		super(type,obj, relation, isDejaVu, b);
	}
	

	@Override
	public void marshall(T obj, TypeRelation relation) throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
			SecurityException, NotImplementedSerializeException {
		if(!isDejaVu){
			writeInt(obj.size());
			for (Object value : obj) {
				traiteObject(value, relation, true);
			}
		}else if (relation == TypeRelation.COMPOSITION){//deja vu, donc on passe ici qd la relation est de type COMPOSITION
			for(Object value : obj){
				traiteObject(value, relation, true);
			}
		}
	}
}
