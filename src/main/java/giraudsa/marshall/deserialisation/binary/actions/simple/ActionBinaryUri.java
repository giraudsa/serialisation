package giraudsa.marshall.deserialisation.binary.actions.simple;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.BinaryUnmarshaller;
import giraudsa.marshall.exception.UnmarshallExeption;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ActionBinaryUri extends ActionBinarySimple<URI> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ActionBinaryUri.class);

	private ActionBinaryUri(Class<URI> type, BinaryUnmarshaller<?> unmarshaller) {
		super(type, unmarshaller);
	}

	public static ActionAbstrait<URI> getInstance(){
		return new ActionBinaryUri(URI.class, null);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <U extends URI> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionBinaryUri(URI.class, (BinaryUnmarshaller<?>) unmarshaller);
	}
	
	@Override
	protected void initialise() throws IOException, UnmarshallExeption{
		if(isDejaVu())
			obj = getObjet();
		else{
			try {
				obj = new URI(readUTF());
			} catch (URISyntaxException e) {
				LOGGER.error(e.getMessage(), e);
				throw new UnmarshallExeption("bad Uri Syntax", e);
			}
			stockeObjetId();
		}
	}
}
