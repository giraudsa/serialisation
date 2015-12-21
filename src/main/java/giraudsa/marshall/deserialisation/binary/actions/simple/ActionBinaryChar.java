package giraudsa.marshall.deserialisation.binary.actions.simple;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.BinaryUnmarshaller;
import java.io.IOException;

public class ActionBinaryChar extends ActionBinarySimple<Character> {

	public static ActionAbstrait<Character> getInstance(BinaryUnmarshaller<?> bu){
		return new ActionBinaryChar(Character.class, bu);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <U extends Character> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionBinaryChar(Character.class, (BinaryUnmarshaller<?>) unmarshaller);
	} 
	
	private ActionBinaryChar(Class<Character> type, BinaryUnmarshaller<?> unmarshaller) {
		super(type, unmarshaller);
	}

	@Override
	protected void initialise() throws IOException {
		obj = readChar();
	}

}
