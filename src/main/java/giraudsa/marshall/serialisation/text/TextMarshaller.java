package giraudsa.marshall.serialisation.text;

import giraudsa.marshall.serialisation.Marshaller;

import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.util.TimeZone;

public abstract class TextMarshaller extends Marshaller {
	protected Writer writer;
	protected DateFormat df;
	private static TimeZone  tz = TimeZone.getTimeZone("UTC");
	
	void write(String string) throws IOException {
		writer.write(string);
	}
	
	protected void dispose() throws IOException {
		writer.close();	
	}
	
	protected TextMarshaller(Writer writer, DateFormat df) {
		super();
		this.writer = writer;
		this.df = df;
		df.setTimeZone(tz);
	}

}
