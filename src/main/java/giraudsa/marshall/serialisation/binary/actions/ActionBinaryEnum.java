package giraudsa.marshall.serialisation.binary.actions;

import giraudsa.marshall.annotations.TypeRelation;
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
	
	public ActionBinaryEnum(Class<? super T> type, BinaryMarshaller b) {
		super(type, b);
	}

	private static final Map<Class<? extends Enum>, Byte> dicoEnumToCodage = new HashMap<>();
	private static final Map<Class<? extends Enum>, Map<Object, Integer>> dicoObjToInteger = new HashMap<>();

	protected void serialise(Object objetASerialiser, TypeRelation typeRelation, boolean couldBeLessSpecific) {
		boolean isDejaVu = writeHeaders(objetASerialiser, typeRelation, couldBeLessSpecific);
		try {
			if(!isDejaVu){
				rempliDictionnaire(objetASerialiser);
				Integer objInt = dicoObjToInteger.get(objetASerialiser.getClass()).get(objetASerialiser);
				if(dicoEnumToCodage.get(objetASerialiser.getClass()) == Constants.Type.CODAGE_BYTE) writeByte(objInt.byteValue());
				else if(dicoEnumToCodage.get(objetASerialiser.getClass()) == Constants.Type.CODAGE_SHORT) writeShort(objInt.shortValue());
				else writeInt(objInt);
			}
		} catch (IOException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected <U> boolean isDejaVu(U objet) {
		return false;
	}

	@SuppressWarnings("unchecked")
	private  void rempliDictionnaire(Object objetASerialiser) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		Map<Object, Integer> map = dicoObjToInteger.get(objetASerialiser.getClass());
		if(map == null){
			map = new HashMap<>();
			dicoObjToInteger.put((Class<? extends Enum>) objetASerialiser.getClass(), map);
			
			Method values = objetASerialiser.getClass().getDeclaredMethod("values");
			T[] listeEnum = (T[]) values.invoke(null);
			int size = listeEnum.length;
			if((int)(byte)size == size) dicoEnumToCodage.put((Class<? extends Enum>) objetASerialiser.getClass(), Constants.Type.CODAGE_BYTE);
			else if((int)(short)size == size) dicoEnumToCodage.put((Class<? extends Enum>) objetASerialiser.getClass(), Constants.Type.CODAGE_SHORT);
			else dicoEnumToCodage.put((Class<? extends Enum>) objetASerialiser.getClass(), Constants.Type.CODAGE_INT);
			int i=0;
			for (T objEnum : listeEnum){
				map.put(objEnum, i++);
			}
		}
	}

}
