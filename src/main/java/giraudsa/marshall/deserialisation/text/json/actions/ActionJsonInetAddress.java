package giraudsa.marshall.deserialisation.text.json.actions;

import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.json.JsonUnmarshaller;
import giraudsa.marshall.deserialisation.text.xml.actions.ActionXmlInetAddress;
import giraudsa.marshall.exception.UnmarshallExeption;

public class ActionJsonInetAddress extends ActionJsonSimpleComportement<InetAddress> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ActionJsonInetAddress.class);
	private ActionJsonInetAddress(Class<InetAddress> type, JsonUnmarshaller<?> jsonUnmarshaller) {
		super(type, jsonUnmarshaller);
	}
	
	@SuppressWarnings("unchecked")
	public static <U> ActionAbstrait<U> getInstance() {	
		return (ActionAbstrait<U>) new ActionJsonInetAddress(InetAddress.class, null);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <U extends InetAddress> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionJsonSimpleComportement<>(InetAddress.class, (JsonUnmarshaller<?>)unmarshaller);
	}
	
	@Override
	protected void rempliData(String donnees) throws ParseException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, UnmarshallExeption {
		try {
			obj = InetAddress.getByName(donnees);
		} catch (UnknownHostException e) {
			LOGGER.error("unknown host destination : "+ donnees, e);
			throw new UnmarshallExeption("unknown host destination : "+ donnees, e);
		}
	}


}
