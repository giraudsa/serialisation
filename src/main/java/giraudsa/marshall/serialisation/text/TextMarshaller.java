package giraudsa.marshall.serialisation.text;

import giraudsa.marshall.serialisation.Marshaller;

import java.io.IOException;
import java.io.Writer;

public abstract class TextMarshaller extends Marshaller {
	protected Writer writer;
	
	void write(String string) throws IOException {
		writer.write(string);
	}
	
	protected void dispose() throws IOException {
		writer.close();	
	}
	
	protected TextMarshaller(Writer writer) {
		this.writer = writer;
	}

}
