package giraudsa.marshall.deserialisation.binary.actions.simple;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.BinaryUnmarshaller;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class ActionBinaryAtomicBoolean extends ActionBinarySimple<AtomicBoolean> {

	private ActionBinaryAtomicBoolean(Class<AtomicBoolean> type, BinaryUnmarshaller<?> unmarshaller) {
		super(type, unmarshaller);
	}

	public static ActionAbstrait<AtomicBoolean> getInstance(){
		return new ActionBinaryAtomicBoolean(AtomicBoolean.class, null);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <U extends AtomicBoolean> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionBinaryAtomicBoolean(AtomicBoolean.class, (BinaryUnmarshaller<?>) unmarshaller);
	}
	
	@Override
	protected void initialise() throws IOException{
		obj = new AtomicBoolean(readBoolean());
	}
}
