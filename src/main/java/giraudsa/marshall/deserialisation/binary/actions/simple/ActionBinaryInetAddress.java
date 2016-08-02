package giraudsa.marshall.deserialisation.binary.actions.simple;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.BinaryUnmarshaller;
import java.io.IOException;
import java.net.InetAddress;


public class ActionBinaryInetAddress<T extends InetAddress> extends ActionBinarySimple<T> {
	
	private ActionBinaryInetAddress(Class<T> type, BinaryUnmarshaller<?> unmarshaller) {
		super(type, unmarshaller);
	}

	public static ActionAbstrait<InetAddress> getInstance(){
		return new ActionBinaryInetAddress<>(InetAddress.class, null);
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public <U extends T> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionBinaryInetAddress<>(type, (BinaryUnmarshaller<?>) unmarshaller);
	}
	
	@Override
	protected void initialise() throws IOException{
		if(isDejaVu())
			obj = getObjet();
		else{
			obj = InetAddress.getByName(readUTF());
			stockeObjetId();
			setDejaTotalementDeSerialise();
		}
	}
}
