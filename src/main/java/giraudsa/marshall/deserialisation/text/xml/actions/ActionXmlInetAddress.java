package giraudsa.marshall.deserialisation.text.xml.actions;


import java.net.InetAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.xml.XmlUnmarshaller;
import giraudsa.marshall.exception.UnmarshallExeption;

public class ActionXmlInetAddress extends ActionXmlSimpleComportement<InetAddress>{
	private static final Logger LOGGER = LoggerFactory.getLogger(ActionXmlInetAddress.class);
	private ActionXmlInetAddress(Class<InetAddress> type, XmlUnmarshaller<?> xmlUnmarshaller) {
		super(type, xmlUnmarshaller);
	}

	@SuppressWarnings("unchecked")
	public static ActionAbstrait<InetAddress> getInstance() {	
		return new ActionXmlInetAddress(InetAddress.class, null);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <U extends InetAddress> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionXmlInetAddress(InetAddress.class, (XmlUnmarshaller<?>)unmarshaller);
	}
	
	@Override protected void construitObjet() throws UnmarshallExeption {
		try {
			obj = InetAddress.getByName(unescapeXml(sb.toString()));
		} catch (UnknownHostException e) {
			LOGGER.error("unknown host destination : "+ unescapeXml(sb.toString()), e);
			throw new UnmarshallExeption("unknown host destination : "+ unescapeXml(sb.toString()), e);
		}
	}

}
