package giraudsa.marshall.serialisation.binary.actions;

import giraudsa.marshall.exception.MarshallExeption;
import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.binary.ActionBinary;


import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import utils.champ.FieldInformations;
import utils.headers.HeaderEnum;

@SuppressWarnings("rawtypes")
public class ActionBinaryEnum extends ActionBinary<Enum> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ActionBinaryEnum.class);
	private static final Map<Class<? extends Enum>, Integer> dicoEnumToCodage = new HashMap<>();
	private static final Map<Class<? extends Enum>, Map<Object, Integer>> dicoObjToInteger = new HashMap<>();

	public ActionBinaryEnum() {
		super();
	}
	
	@Override
	protected boolean writeHeaders(Marshaller marshaller, Enum objetASerialiser, FieldInformations fieldInformations)
			throws MarshallExeption, IOException {
		Class<?> typeObj = getTypeObjProblemeHibernate(objetASerialiser);
		boolean isTypeDevinable = isTypeDevinable(marshaller, objetASerialiser, fieldInformations);
		boolean isDejaVuType = isDejaVuType(marshaller, typeObj);
		short smallIdType = getSmallIdTypeAndStockType(marshaller, typeObj);
		HeaderEnum<?> header = HeaderEnum.getHeader(smallIdType, isTypeDevinable);
		header.write(getOutput(marshaller), smallIdType, typeObj, isDejaVuType);
		return false;
	}

	@Override
	protected void ecritValeur(Marshaller marshaller, Enum enumASerialiser, FieldInformations fieldInformations, boolean isDejaVu) throws IOException, MarshallExeption {
		rempliDictionnaire(enumASerialiser);
		Integer objInt = dicoObjToInteger.get(enumASerialiser.getClass()).get(enumASerialiser);
		if(dicoEnumToCodage.get(enumASerialiser.getClass()) == 1) 
			writeByte(marshaller, objInt.byteValue());
		else if(dicoEnumToCodage.get(enumASerialiser.getClass()) == 2)
			writeShort(marshaller, objInt.shortValue());
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
				dicoEnumToCodage.put(clazz, 1);
			else if((int)(short)size == size)
				dicoEnumToCodage.put(clazz, 2);
			int i=0;
			for (Enum objEnum : listeEnum){
				map.put(objEnum, i++);
			}
		}
	}

}
