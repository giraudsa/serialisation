package giraudsa.marshall.serialisation.text;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.MarshallExeption;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.strategie.StrategieDeSerialisation;
import utils.ConfigurationMarshalling;
import utils.EntityManager;
import utils.champ.FakeChamp;

public abstract class TextMarshaller extends Marshaller {
	protected final DateFormat df;
	protected final boolean isUniversalId;
	// prettyPrint
	protected boolean lastIsOpen = false;

	protected final Writer writer;

	protected TextMarshaller(final Writer writer, final SimpleDateFormat dateFormat,
			final StrategieDeSerialisation strategie, final EntityManager entityManager) {
		super(strategie, entityManager);
		this.writer = writer;
		df = new SimpleDateFormat(dateFormat.toPattern());
		df.setTimeZone(dateFormat.getTimeZone());
		isUniversalId = ConfigurationMarshalling.getEstIdUniversel();
	}

	protected void dispose() throws IOException {
		writer.close();
	}

	protected boolean isPrettyPrint() {
		return ConfigurationMarshalling.isPrettyPrint();
	}

	protected <U> void marshall(final U obj)
			throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException,
			IOException, NotImplementedSerializeException, MarshallExeption {
		if (obj == null)
			return;
		final FakeChamp fieldsInfo = new FakeChamp(null, Object.class, TypeRelation.COMPOSITION, null);
		marshall(obj, fieldsInfo);
		while (!aFaire.isEmpty())
			deserialisePile();
	}

	protected void write(final char c) throws IOException {
		writer.write(c);
	}

	protected void write(final String string) throws IOException {
		writer.write(string);
	}
}
