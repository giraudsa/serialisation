package giraudsa.marshall.deserialisation.text.xml.actions;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.xml.XmlUnmarshaller;
import giraudsa.marshall.exception.InstanciationException;

public class ActionXmlInetAddress extends ActionXmlSimpleComportement<InetAddress> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ActionXmlInetAddress.class);

	@SuppressWarnings("unchecked")
	public static ActionAbstrait<InetAddress> getInstance() {
		return new ActionXmlInetAddress(InetAddress.class, null);
	}

	private ActionXmlInetAddress(final Class<InetAddress> type, final XmlUnmarshaller<?> xmlUnmarshaller) {
		super(type, xmlUnmarshaller);
	}

	@Override
	protected void construitObjet() throws InstanciationException {
		try {
			obj = InetAddress.getByName(unescapeXml(sb.toString()));
		} catch (final UnknownHostException e) {
			LOGGER.error("unknown host destination : " + unescapeXml(sb.toString()), e);
			throw new InstanciationException("unknown host destination : " + unescapeXml(sb.toString()), e);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <U extends InetAddress> ActionAbstrait<U> getNewInstance(final Class<U> type,
			final Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionXmlInetAddress(InetAddress.class, (XmlUnmarshaller<?>) unmarshaller);
	}

}
