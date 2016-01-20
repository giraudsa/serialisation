package giraudsa.marshall.deserialisation.binary.actions.simple;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.BinaryUnmarshaller;
import giraudsa.marshall.exception.UnmarshallExeption;

import java.io.IOException;
import java.net.InetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ActionBinaryInetAddress extends ActionBinarySimple<InetAddress> {
	
	private ActionBinaryInetAddress(Class<InetAddress> type, BinaryUnmarshaller<?> unmarshaller) {
		super(type, unmarshaller);
	}

	public static ActionAbstrait<InetAddress> getInstance(){
		return new ActionBinaryInetAddress(InetAddress.class, null);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <U extends InetAddress> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionBinaryInetAddress(InetAddress.class, (BinaryUnmarshaller<?>) unmarshaller);
	}
	
	@Override
	protected void initialise() throws IOException, UnmarshallExeption{
		if(isDejaVu())
			obj = getObjet();
		else{
			obj = InetAddress.getByName(readUTF());
			stockeObjetId();
		}
	}
}
