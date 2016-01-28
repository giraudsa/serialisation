package giraudsa.marshall.deserialisation.binary.actions;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.BinaryUnmarshaller;
import giraudsa.marshall.deserialisation.binary.actions.simple.ActionBinarySimple;

import java.io.IOException;

@SuppressWarnings("rawtypes")
public class ActionBinaryEnum<E extends Enum> extends ActionBinarySimple<E> {
	
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
		Enum[] enums = type.getEnumConstants();
		if(enums.length < 254) 
			obj =  enums[(int)readByte()];
		else 
			obj = enums[(int)readShort()];
	}
}
