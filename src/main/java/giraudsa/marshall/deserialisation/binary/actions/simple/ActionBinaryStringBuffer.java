package giraudsa.marshall.deserialisation.binary.actions.simple;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.BinaryUnmarshaller;
import java.io.IOException;

public class ActionBinaryStringBuffer extends ActionBinarySimple<StringBuffer> {

	private ActionBinaryStringBuffer(Class<StringBuffer> type, BinaryUnmarshaller<?> unmarshaller) {
		super(type, unmarshaller);
	}

	public static ActionAbstrait<StringBuffer> getInstance(){
		return new ActionBinaryStringBuffer(StringBuffer.class, null);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <U extends StringBuffer> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionBinaryStringBuffer(StringBuffer.class, (BinaryUnmarshaller<?>) unmarshaller);
	}
	
	@Override
	protected void initialise() throws IOException{
		if(isDejaVu())
			obj = getObjet();
		else{
			obj = new StringBuffer(readUTF());
			stockeObjetId();
		}
	}
}
