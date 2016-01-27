package giraudsa.marshall.deserialisation.binary.actions.simple;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.BinaryUnmarshaller;
import java.io.IOException;

public class ActionBinaryStringBuilder extends ActionBinarySimple<StringBuilder> {

	private ActionBinaryStringBuilder(Class<StringBuilder> type, BinaryUnmarshaller<?> unmarshaller) {
		super(type, unmarshaller);
	}

	public static ActionAbstrait<StringBuilder> getInstance(){
		return new ActionBinaryStringBuilder(StringBuilder.class, null);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <U extends StringBuilder> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionBinaryStringBuilder(StringBuilder.class, (BinaryUnmarshaller<?>) unmarshaller);
	}
	
	@Override
	protected void initialise() throws IOException{
		if(isDejaVu())
			obj = getObjet();
		else{
			obj = new StringBuilder(readUTF());
			stockeObjetId();
			setDejaTotalementDeSerialise();
		}
	}
}
