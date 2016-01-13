package giraudsa.marshall.deserialisation.binary.actions.simple;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.BinaryUnmarshaller;
import java.io.IOException;

public class ActionBinaryLong extends ActionBinarySimple<Long> {

	private ActionBinaryLong(Class<Long> type, BinaryUnmarshaller<?> unmarshaller) {
		super(type, unmarshaller);
	}

	public static ActionAbstrait<Long> getInstance(BinaryUnmarshaller<?> bu){
		return new ActionBinaryLong(Long.class, bu);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <U extends Long> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionBinaryLong(Long.class, (BinaryUnmarshaller<?>) unmarshaller);
	}

	@Override
	protected void initialise() throws IOException {
		obj = readLong();
	}

}
