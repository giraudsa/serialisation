package giraudsa.marshall.serialisation.binary.actions;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.binary.ActionBinary;
import giraudsa.marshall.serialisation.binary.BinaryMarshaller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import utils.Constants;

@SuppressWarnings("rawtypes")
public class ActionBinaryEnum<T extends Enum> extends ActionBinary<T> {
	
	private byte typeSousJacent;
	private Map<T, Integer> dicoObjToInteger = new HashMap<>();

	@SuppressWarnings("unchecked")
	public ActionBinaryEnum(Class<? super T> type, T obj, TypeRelation relation, Boolean isDejaVu, BinaryMarshaller b) throws IOException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException {
		super(type, obj, relation, isDejaVu, b);
		Method values = type.getDeclaredMethod("values");
		T[] listeEnum = (T[]) values.invoke(null);
		int size = listeEnum.length;
		if((int)(byte)size == size) typeSousJacent = Constants.Type.CODAGE_BYTE;
		else if((int)(short)size == size) typeSousJacent = Constants.Type.CODAGE_SHORT;
		else typeSousJacent = Constants.Type.CODAGE_INT;
		int i=0;
		for (T objEnum : listeEnum){
			dicoObjToInteger.put(objEnum, i++);
		}
	}

	@Override
	public void marshall(T obj, TypeRelation relation) throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
			SecurityException, NotImplementedSerializeException {
		Integer objInt = dicoObjToInteger.get(obj);
		if(typeSousJacent == Constants.Type.CODAGE_BYTE) writeByte(objInt.byteValue());
		else if(typeSousJacent == Constants.Type.CODAGE_SHORT) writeShort(objInt.shortValue());
		else writeInt(objInt);
	}

}
