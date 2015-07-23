package giraudsa.marshall.serialisation.binary.actions.simple;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.binary.BinaryMarshaller;
import giraudsa.marshall.serialisation.binary.actions.ActionBinarySimpleComportement;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class ActionBinaryChar  extends ActionBinarySimpleComportement<Character>{

	public ActionBinaryChar(Class<? super Character> type, Object obj, TypeRelation relation, Boolean isDejaVu, BinaryMarshaller b) throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException {
		super(type,obj, TypeRelation.COMPOSITION, isDejaVu, b);
	}
	@Override
	public void marshall(Character obj, TypeRelation relation) throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, NotImplementedSerializeException {
		writeChar(obj);
	}
}
