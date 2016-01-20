package giraudsa.marshall.deserialisation.binary.actions.simple;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.BinaryUnmarshaller;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class ActionBinaryAtomicInteger extends ActionBinarySimple<AtomicInteger> {

	private ActionBinaryAtomicInteger(Class<AtomicInteger> type, BinaryUnmarshaller<?> unmarshaller) {
		super(type, unmarshaller);
	}

	public static ActionAbstrait<AtomicInteger> getInstance(){
		return new ActionBinaryAtomicInteger(AtomicInteger.class, null);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <U extends AtomicInteger> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionBinaryAtomicInteger(AtomicInteger.class, (BinaryUnmarshaller<?>) unmarshaller);
	}
	
	@Override
	protected void initialise() throws IOException{
		obj = new AtomicInteger(readInt());
	}
}
