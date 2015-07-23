package giraudsa.marshall.serialisation.binary.actions.simple;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.binary.BinaryMarshaller;
import giraudsa.marshall.serialisation.binary.actions.ActionBinarySimpleComportement;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class ActionBinaryInteger  extends ActionBinarySimpleComportement<Integer>{

	public ActionBinaryInteger(Class<? super Integer> type, Integer obj, TypeRelation relation, Boolean isDejaVu, BinaryMarshaller b) throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException {
		super(type,obj, TypeRelation.COMPOSITION, isDejaVu, b);
	}
	
	@Override
	public void marshall(Integer obj, TypeRelation relation) throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
	NoSuchMethodException, SecurityException, NotImplementedSerializeException {
		writeInt(obj);
	}

}
