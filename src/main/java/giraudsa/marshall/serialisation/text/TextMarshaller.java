package giraudsa.marshall.serialisation.text;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.MarshallExeption;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.strategie.StrategieDeSerialisation;
import utils.ConfigurationMarshalling;
import utils.champ.FakeChamp;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public abstract class TextMarshaller extends Marshaller {
	protected final Writer writer;
	protected final DateFormat df;
	protected final boolean isUniversalId;
	
	//prettyPrint
	protected boolean lastIsOpen = false;
	
	
	protected TextMarshaller(Writer writer, SimpleDateFormat dateFormat, StrategieDeSerialisation strategie) {
		super(strategie);
		this.writer = writer;
		df = new SimpleDateFormat(dateFormat.toPattern());
		df.setTimeZone(dateFormat.getTimeZone());
		this.isUniversalId = ConfigurationMarshalling.getEstIdUniversel();
	}

	protected void write(String string) throws IOException {
		writer.write(string);
	}
	protected void write(char c) throws IOException {
		writer.write(c);
	}
	
	protected void dispose() throws IOException {
		writer.close();	
	}
	
	protected <U> void marshall(U obj) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException, NotImplementedSerializeException, MarshallExeption{
		if (obj == null)
			return;
		FakeChamp fieldsInfo = new FakeChamp(null, Object.class, TypeRelation.COMPOSITION, null);
		marshall(obj, fieldsInfo);
		while(!aFaire.isEmpty()){
			deserialisePile();
		}
	}
	
	protected boolean isPrettyPrint(){
		return ConfigurationMarshalling.isPrettyPrint();
	}
}
