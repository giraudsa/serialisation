package giraudsa.marshall.serialisation.text;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.Marshaller;
import utils.ConfigurationMarshalling;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public abstract class TextMarshaller extends Marshaller {
	protected final Writer writer;
	protected final DateFormat df;
	final boolean isUniversalId;
	
	
	void write(String string) throws IOException {
		writer.write(string);
	}
	
	protected void dispose() throws IOException {
		writer.close();	
	}
	
	protected TextMarshaller(Writer writer, boolean isCompleteSerialisation, SimpleDateFormat dateFormat) {
		super(isCompleteSerialisation);
		this.writer = writer;
		df = new SimpleDateFormat(dateFormat.toPattern());
		df.setTimeZone(dateFormat.getTimeZone());
		this.isUniversalId = ConfigurationMarshalling.getEstIdUniversel();
		
	}
	
	protected <U> void marshall(U obj) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, IOException, NotImplementedSerializeException {
		if (obj == null) return;
		marshallSpecialise(obj, TypeRelation.COMPOSITION, null, false);
		while(!aFaire.isEmpty()){
			DeserialisePile();
		}
	}

	public <T> void marshallSpecialise(T obj, TypeRelation relation, String nom, boolean typeDevinable) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, IOException, NotImplementedSerializeException {
		ActionText<?> action = (ActionText<?>) getAction(obj);
		action.marshall(obj, relation, nom, typeDevinable);
	}

}
