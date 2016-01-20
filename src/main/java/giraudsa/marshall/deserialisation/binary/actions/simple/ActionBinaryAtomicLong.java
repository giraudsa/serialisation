package giraudsa.marshall.deserialisation.binary.actions.simple;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.BinaryUnmarshaller;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

public class ActionBinaryAtomicLong extends ActionBinarySimple<AtomicLong> {

	private ActionBinaryAtomicLong(Class<AtomicLong> type, BinaryUnmarshaller<?> unmarshaller) {
		super(type, unmarshaller);
	}

	public static ActionAbstrait<AtomicLong> getInstance(){
		return new ActionBinaryAtomicLong(AtomicLong.class, null);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <U extends AtomicLong> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionBinaryAtomicLong(AtomicLong.class, (BinaryUnmarshaller<?>) unmarshaller);
	}
	
	@Override
	protected void initialise() throws IOException{
		obj = new AtomicLong(readLong());
	}
}
