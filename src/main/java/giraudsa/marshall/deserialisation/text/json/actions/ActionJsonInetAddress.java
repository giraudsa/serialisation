package giraudsa.marshall.deserialisation.text.json.actions;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.json.JsonUnmarshaller;
import giraudsa.marshall.exception.InstanciationException;

public class ActionJsonInetAddress<T extends InetAddress> extends ActionJsonSimpleComportement<T> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ActionJsonInetAddress.class);

	@SuppressWarnings("unchecked")
	public static <U> ActionAbstrait<U> getInstance() {
		return (ActionAbstrait<U>) new ActionJsonInetAddress<>(InetAddress.class, null);
	}

	private ActionJsonInetAddress(final Class<T> type, final JsonUnmarshaller<?> jsonUnmarshaller) {
		super(type, jsonUnmarshaller);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public <U extends T> ActionAbstrait<U> getNewInstance(final Class<U> type, final Unmarshaller unmarshaller) {
		return new ActionJsonInetAddress<>(type, (JsonUnmarshaller<?>) unmarshaller);
	}

	@Override
	protected void rempliData(final String donnees) throws InstanciationException {
		try {
			obj = InetAddress.getByName(donnees);
		} catch (final UnknownHostException e) {
			LOGGER.error("unknown host destination : " + donnees, e);
			throw new InstanciationException("unknown host destination : " + donnees, e);
		}
	}

}
