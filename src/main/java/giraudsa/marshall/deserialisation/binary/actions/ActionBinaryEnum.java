package giraudsa.marshall.deserialisation.binary.actions;

import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.ActionBinary;
import giraudsa.marshall.exception.NotImplementedSerializeException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import utils.Constants;

@SuppressWarnings("rawtypes")
public class ActionBinaryEnum<T extends Enum> extends ActionBinary<T> {

	private byte typeSousJacent;
	private T[] listeEnum;

	public ActionBinaryEnum(Class<? extends T> type, Unmarshaller<?> unmarshaller) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		super(type, unmarshaller);
	}

	@Override
	protected T readObject() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException,
	ClassNotFoundException, IOException, NotImplementedSerializeException {
		rempliListeEnum();
		if(typeSousJacent == Constants.Type.CODAGE_BYTE) return listeEnum[(int)readByte()];
		if(typeSousJacent == Constants.Type.CODAGE_SHORT) return listeEnum[(int)readShort()];
		return listeEnum[readInt()];
	}

	@SuppressWarnings("unchecked")
	private void rempliListeEnum() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		if(listeEnum == null){
			Method values = type.getDeclaredMethod("values");
			listeEnum = (T[]) values.invoke(null);
			int size = listeEnum.length;
			if((int)(byte)size == size) typeSousJacent = Constants.Type.CODAGE_BYTE;
			else if((int)(short)size == size) typeSousJacent = Constants.Type.CODAGE_SHORT;
			else typeSousJacent = Constants.Type.CODAGE_INT;
		}
	}

}
