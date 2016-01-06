package giraudsa.marshall.serialisation.text;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.Marshaller;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.util.TimeZone;

public abstract class TextMarshaller extends Marshaller {
	protected final Writer writer;
	protected final DateFormat df;
	final boolean isUniversalId;
	
	private static boolean idEstUniversel = true;
	public static void setIdDependantDuType(){
		idEstUniversel = false;
	}
	
	void write(String string) throws IOException {
		writer.write(string);
	}
	
	protected void dispose() throws IOException {
		writer.close();	
	}
	
	protected TextMarshaller(Writer writer, DateFormat df, boolean isCompleteSerialisation) {
		super(isCompleteSerialisation);
		this.writer = writer;
		this.df = df;
		df.setTimeZone(TimeZone.getTimeZone("UTC"));
		this.isUniversalId = idEstUniversel;
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
