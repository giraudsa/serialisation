package giraudsa.marshall.serialisation.binary.actions;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.binary.ActionBinary;
import giraudsa.marshall.serialisation.binary.BinaryMarshaller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class ActionBinarySimpleComportement<T> extends ActionBinary<T> {

	public ActionBinarySimpleComportement(Class<? super T> type, Object obj, TypeRelation relation, Boolean isDejaVu, BinaryMarshaller b) throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException {
		super(type,obj, TypeRelation.COMPOSITION, isDejaVu, b);
	}

	@Override
	public void marshall(T obj, TypeRelation relation) throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
			SecurityException, NotImplementedSerializeException {
		if(!isDejaVu) writeUTF(obj.toString());
	}
}
