package giraudsa.marshall.serialisation.binary.actions.simple;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.binary.BinaryMarshaller;
import giraudsa.marshall.serialisation.binary.actions.ActionBinarySimpleComportement;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class ActionBinaryDouble  extends ActionBinarySimpleComportement<Double> {

	public ActionBinaryDouble(Class<? super Double> type, Object obj, TypeRelation relation, Boolean isDejaVu, BinaryMarshaller b) throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException {
		super(type,obj, TypeRelation.COMPOSITION, isDejaVu, b);
	}
	@Override
	public void marshall(Double obj, TypeRelation relation) throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, NotImplementedSerializeException {
		writeDouble(obj);
	}
	
}
