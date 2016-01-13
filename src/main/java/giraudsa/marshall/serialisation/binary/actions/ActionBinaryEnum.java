package giraudsa.marshall.serialisation.binary.actions;

import giraudsa.marshall.exception.MarshallExeption;
import giraudsa.marshall.serialisation.binary.ActionBinary;
import giraudsa.marshall.serialisation.binary.BinaryMarshaller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import utils.Constants;
import utils.champ.FieldInformations;

@SuppressWarnings("rawtypes")
public class ActionBinaryEnum extends ActionBinary<Enum> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ActionBinaryEnum.class);
	private static final Map<Class<? extends Enum>, Byte> dicoEnumToCodage = new HashMap<>();
	private static final Map<Class<? extends Enum>, Map<Object, Integer>> dicoObjToInteger = new HashMap<>();

	public ActionBinaryEnum(BinaryMarshaller b) {
		super(b);
	}

	@Override
	protected void ecritValeur(Enum enumASerialiser, FieldInformations fieldInformations) throws IOException, MarshallExeption {
		if(!isDejaTotalementSerialise(enumASerialiser)){
				setDejaTotalementSerialise(enumASerialiser);
				rempliDictionnaire(enumASerialiser);
				Integer objInt = dicoObjToInteger.get(enumASerialiser.getClass()).get(enumASerialiser);
				if(dicoEnumToCodage.get(enumASerialiser.getClass()) == Constants.Type.CODAGE_BYTE) 
					writeByte(objInt.byteValue());
				else if(dicoEnumToCodage.get(enumASerialiser.getClass()) == Constants.Type.CODAGE_SHORT)
					writeShort(objInt.shortValue());
				else writeInt(objInt);
		}
	}
	
	@Override
	protected <U> boolean isDejaVu(U objet) {
		return false;
	}

	@SuppressWarnings("unchecked")
	private static synchronized void rempliDictionnaire(Object objetASerialiser) throws MarshallExeption{
		Map<Object, Integer> map = dicoObjToInteger.get(objetASerialiser.getClass());
		if(map == null){
			map = new HashMap<>();
			Class<? extends Enum> clazz = (Class<? extends Enum>) objetASerialiser.getClass();
			dicoObjToInteger.put(clazz , map);
			
			Method values;
			Enum[] listeEnum = null;
			try {
				values = clazz.getDeclaredMethod("values");
				listeEnum = (Enum[]) values.invoke(null);
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				LOGGER.error("impossible de récupérer les valeurs de l'enum", e);
				throw new MarshallExeption(e);
			}
			int size = listeEnum.length;
			if((int)(byte)size == size)
				dicoEnumToCodage.put(clazz, Constants.Type.CODAGE_BYTE);
			else if((int)(short)size == size)
				dicoEnumToCodage.put(clazz, Constants.Type.CODAGE_SHORT);
			else 
				dicoEnumToCodage.put(clazz, Constants.Type.CODAGE_INT);
			int i=0;
			for (Enum objEnum : listeEnum){
				map.put(objEnum, i++);
			}
		}
	}

}
