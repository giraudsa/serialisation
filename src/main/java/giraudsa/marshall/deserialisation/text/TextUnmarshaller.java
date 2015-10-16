package giraudsa.marshall.deserialisation.text;

import giraudsa.marshall.deserialisation.EntityManager;
import giraudsa.marshall.deserialisation.Unmarshaller;

import java.io.IOException;
import java.io.Reader;
import java.text.DateFormat;
import java.util.TimeZone;

public class TextUnmarshaller<T> extends Unmarshaller<T> {

	protected final Reader reader;
	protected DateFormat df;
	private static TimeZone  tz = TimeZone.getTimeZone("UTC");
	
	protected TextUnmarshaller(Reader reader, DateFormat df) throws ClassNotFoundException {
		super();
		this.reader = reader;
		this.df = df;
		df.setTimeZone(tz);
	}
	
	protected TextUnmarshaller(Reader reader, EntityManager entity, DateFormat df) throws ClassNotFoundException {
		super(entity);
		this.reader = reader;
		this.df = df;
		df.setTimeZone(tz);
	}
	
	public void dispose() throws IOException {
		reader.close();	
	}	

}
