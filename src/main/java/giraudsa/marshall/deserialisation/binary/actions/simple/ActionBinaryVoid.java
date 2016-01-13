package giraudsa.marshall.deserialisation.binary.actions.simple;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.BinaryUnmarshaller;

public class ActionBinaryVoid extends ActionBinarySimple<Void> {

	private ActionBinaryVoid(Class<Void> type, BinaryUnmarshaller<?> unmarshaller) {
		super(type, unmarshaller);
	}

	public static ActionAbstrait<Void> getInstance(BinaryUnmarshaller<?> bu){
		return new ActionBinaryVoid(Void.class, bu);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <U extends Void> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionBinaryVoid(Void.class, (BinaryUnmarshaller<?>)unmarshaller);
	}
	
	@Override
	protected void initialise() {
		obj = null;
	}

}
