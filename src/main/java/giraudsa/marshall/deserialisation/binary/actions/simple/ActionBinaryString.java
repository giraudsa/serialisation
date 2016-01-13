package giraudsa.marshall.deserialisation.binary.actions.simple;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.BinaryUnmarshaller;
import java.io.IOException;

public class ActionBinaryString extends ActionBinarySimple<String> {

	private ActionBinaryString(Class<String> type, BinaryUnmarshaller<?> unmarshaller) {
		super(type, unmarshaller);
	}

	public static ActionAbstrait<String> getInstance(BinaryUnmarshaller<?> bu){
		return new ActionBinaryString(String.class, bu);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <U extends String> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionBinaryString(String.class, (BinaryUnmarshaller<?>) unmarshaller);
	}
	
	@Override
	protected void initialise() throws IOException{
		boolean isDejaVu = isDejaVu();
		if(isDejaVu)
			obj = getObjetDejaVu();
		else{
			obj = readUTF();
			stockeObjetId();
		}
	}
}
