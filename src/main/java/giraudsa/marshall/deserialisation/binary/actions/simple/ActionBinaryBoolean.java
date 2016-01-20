package giraudsa.marshall.deserialisation.binary.actions.simple;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.BinaryUnmarshaller;
import utils.Constants;

public class ActionBinaryBoolean extends ActionBinarySimple<Boolean> {

	private ActionBinaryBoolean(Class<Boolean> type, BinaryUnmarshaller<?> unmarshaller) {
		super(type, unmarshaller);
	}

	public static ActionAbstrait<Boolean> getInstance(){
		return new ActionBinaryBoolean(Boolean.class, null);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <U extends Boolean> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionBinaryBoolean(Boolean.class, (BinaryUnmarshaller<?>)unmarshaller);
	}
	
	public void setBool(byte header) {
		obj = (header == Constants.BoolValue.TRUE)? true : false;
	}

	@Override
	protected void initialise() {
		//rien a faire
	}

}
