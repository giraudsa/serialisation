package giraudsa.marshall.deserialisation.binary.actions;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.ActionBinary;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import utils.Constants;

@SuppressWarnings("rawtypes")
public class ActionBinaryEnum extends ActionBinary<Enum> {

	private Map<Class<?>, Byte> mapTypeSousJacent = new HashMap<>();
	private Map<Class<?>, Enum[]> mapListeEnum = new HashMap<>();

	public ActionBinaryEnum(Class<Enum> type, Unmarshaller<?> unmarshaller){
		super(type, unmarshaller);
	}

	@Override
	protected Enum readObject(Class<? extends Enum> typeADeserialiser, TypeRelation typeRelation, int smallId){
		try {
			rempliListeEnum(typeADeserialiser);
			byte typeSousJacent = mapTypeSousJacent.get(typeADeserialiser);
			if(typeSousJacent == Constants.Type.CODAGE_BYTE) return mapListeEnum.get(typeADeserialiser)[(int)readByte()];
			if(typeSousJacent == Constants.Type.CODAGE_SHORT) return mapListeEnum.get(typeADeserialiser)[(int)readShort()];
			return mapListeEnum.get(typeADeserialiser)[readInt()];
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void rempliListeEnum(Class<? extends Enum> typeADeserialiser) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		if(mapListeEnum.get(typeADeserialiser) == null){
			Method values = typeADeserialiser.getDeclaredMethod("values");
			mapListeEnum.put(typeADeserialiser, (Enum[]) values.invoke(null));
			int size = mapListeEnum.get(typeADeserialiser).length;
			if((int)(byte)size == size) mapTypeSousJacent.put(typeADeserialiser, Constants.Type.CODAGE_BYTE);
			else if((int)(short)size == size) mapTypeSousJacent.put(typeADeserialiser, Constants.Type.CODAGE_SHORT);
			else mapTypeSousJacent.put(typeADeserialiser, Constants.Type.CODAGE_INT);
		}
	}

}
