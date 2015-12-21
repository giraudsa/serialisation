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
public class ActionBinaryEnum extends ActionBinary<Enum> {
	
	public ActionBinaryEnum(BinaryMarshaller b) {
		super(b);
	}

	private static final Map<Class<? extends Enum>, Byte> dicoEnumToCodage = new HashMap<>();
	private static final Map<Class<? extends Enum>, Map<Object, Integer>> dicoObjToInteger = new HashMap<>();

	protected void ecritValeur(Enum enumASerialiser, TypeRelation typeRelation) throws IOException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if(!isDejaTotalementSerialise(enumASerialiser)){
				setDejaTotalementSerialise(enumASerialiser);
				rempliDictionnaire(enumASerialiser);
				Integer objInt = dicoObjToInteger.get(enumASerialiser.getClass()).get(enumASerialiser);
				if(dicoEnumToCodage.get(enumASerialiser.getClass()) == Constants.Type.CODAGE_BYTE) writeByte(objInt.byteValue());
				else if(dicoEnumToCodage.get(enumASerialiser.getClass()) == Constants.Type.CODAGE_SHORT) writeShort(objInt.shortValue());
				else writeInt(objInt);
		}
	}
	
	@Override
	protected <U> boolean isDejaVu(U objet) {
		return false;
	}

	@SuppressWarnings("unchecked")
	private  void rempliDictionnaire(Object objetASerialiser) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		Map<Object, Integer> map = dicoObjToInteger.get(objetASerialiser.getClass());
		if(map == null){
			map = new HashMap<>();
			dicoObjToInteger.put((Class<? extends Enum>) objetASerialiser.getClass(), map);
			
			Method values = objetASerialiser.getClass().getDeclaredMethod("values");
			Enum[] listeEnum = (Enum[]) values.invoke(null);
			int size = listeEnum.length;
			if((int)(byte)size == size) dicoEnumToCodage.put((Class<? extends Enum>) objetASerialiser.getClass(), Constants.Type.CODAGE_BYTE);
			else if((int)(short)size == size) dicoEnumToCodage.put((Class<? extends Enum>) objetASerialiser.getClass(), Constants.Type.CODAGE_SHORT);
			else dicoEnumToCodage.put((Class<? extends Enum>) objetASerialiser.getClass(), Constants.Type.CODAGE_INT);
			int i=0;
			for (Enum objEnum : listeEnum){
				map.put(objEnum, i++);
			}
		}
	}

}
