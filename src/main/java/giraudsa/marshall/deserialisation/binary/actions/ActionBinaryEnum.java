package giraudsa.marshall.deserialisation.binary.actions;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.BinaryUnmarshaller;
import giraudsa.marshall.deserialisation.binary.actions.simple.ActionBinarySimple;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import utils.Constants;

@SuppressWarnings("rawtypes")
public class ActionBinaryEnum<E extends Enum> extends ActionBinarySimple<E> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ActionBinaryEnum.class);
	private Map<Class<?>, Byte> mapTypeSousJacent = new HashMap<>();
	private Map<Class<?>, Enum[]> mapListeEnum = new HashMap<>();
	
	private ActionBinaryEnum(Class<E> type, BinaryUnmarshaller<?> unmarshaller){
		super(type, unmarshaller);
	}

	public static ActionAbstrait<Enum> getInstance(){
		return new ActionBinaryEnum<>(Enum.class, null);
	}
	
	@Override
	public <U extends E> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return new ActionBinaryEnum<>(type, (BinaryUnmarshaller<?>) unmarshaller);
	}

	@Override
	protected void initialise() throws IOException, InstantiationException, IllegalAccessException {
		try {
			rempliListeEnum();
			byte typeSousJacent = mapTypeSousJacent.get(type);
			if(typeSousJacent == Constants.Type.CODAGE_BYTE) 
				obj =  mapListeEnum.get(type)[(int)readByte()];
			else if(typeSousJacent == Constants.Type.CODAGE_SHORT) 
				obj = mapListeEnum.get(type)[(int)readShort()];
			else 
				obj = mapListeEnum.get(type)[readInt()];
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | IOException e) {
			LOGGER.error("T n'est pas un Enum... étrange", e);
		}
	}
	
	
	

	private void rempliListeEnum() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException{
		if(mapListeEnum.get(type) == null){
			Method values = type.getDeclaredMethod("values");
			mapListeEnum.put(type, (Enum[]) values.invoke(null));
			int size = mapListeEnum.get(type).length;
			if((int)(byte)size == size)
				mapTypeSousJacent.put(type, Constants.Type.CODAGE_BYTE);
			else if((int)(short)size == size)
				mapTypeSousJacent.put(type, Constants.Type.CODAGE_SHORT);
			else mapTypeSousJacent.put(type, Constants.Type.CODAGE_INT);
		}
	}
}
