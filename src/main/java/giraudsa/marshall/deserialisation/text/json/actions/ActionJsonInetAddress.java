package giraudsa.marshall.deserialisation.text.json.actions;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.json.JsonUnmarshaller;
import giraudsa.marshall.exception.UnmarshallExeption;

public class ActionJsonInetAddress<T extends InetAddress> extends ActionJsonSimpleComportement<T> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ActionJsonInetAddress.class);
	private ActionJsonInetAddress(Class<T> type, JsonUnmarshaller<?> jsonUnmarshaller) {
		super(type, jsonUnmarshaller);
	}
	
	@SuppressWarnings("unchecked")
	public static <U> ActionAbstrait<U> getInstance() {	
		return (ActionAbstrait<U>) new ActionJsonInetAddress<>(InetAddress.class, null);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public <U extends T> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionJsonInetAddress<>(type, (JsonUnmarshaller<?>)unmarshaller);
	}
	
	@Override
	protected void rempliData(String donnees) throws UnmarshallExeption {
		try {
			obj = InetAddress.getByName(donnees);
		} catch (UnknownHostException e) {
			LOGGER.error("unknown host destination : "+ donnees, e);
			throw new UnmarshallExeption("unknown host destination : "+ donnees, e);
		}
	}


}
