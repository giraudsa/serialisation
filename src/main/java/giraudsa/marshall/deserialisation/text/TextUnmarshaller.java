package giraudsa.marshall.deserialisation.text;

import giraudsa.marshall.deserialisation.EntityManager;
import giraudsa.marshall.deserialisation.Unmarshaller;

import java.io.IOException;
import java.io.Reader;

public class TextUnmarshaller<T> extends Unmarshaller<T> {

	protected final Reader reader;
	
	protected TextUnmarshaller(Reader reader) throws ClassNotFoundException {
		super();
		this.reader = reader;
	}
	
	protected TextUnmarshaller(Reader reader, EntityManager entity) throws ClassNotFoundException {
		super(entity);
		this.reader = reader;
	}
	
	public void dispose() throws IOException {
		reader.close();	
	}	

}
