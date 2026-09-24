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
import utils.CopieFormatDate;
import utils.EntityManager;
import utils.champ.FakeChamp;
import utils.io.SortieTexte;

public abstract class TextMarshaller extends Marshaller {
	/** Sortie tamponnée non synchronisée (vidée vers le Writer de l'appelant par flush, en fin de sérialisation). */
	private static Writer bufferise(final Writer writer) {
		if (writer instanceof SortieTexte)
			return writer;
		return new SortieTexte(writer);
	}

	protected final DateFormat df;
	protected final boolean isUniversalId;
	private final boolean prettyPrint;
	// prettyPrint
	protected boolean lastIsOpen = false;

	protected final Writer writer;

	protected TextMarshaller(final Writer writer, final SimpleDateFormat dateFormat,
			final StrategieDeSerialisation strategie, final EntityManager entityManager) {
		super(strategie, entityManager);
		this.writer = bufferise(writer);
		df = CopieFormatDate.copie(dateFormat);
		isUniversalId = ConfigurationMarshalling.getEstIdUniversel();
		prettyPrint = ConfigurationMarshalling.isPrettyPrint();
	}

	protected void dispose() throws IOException {
		writer.close();
	}

	protected boolean isPrettyPrint() {
		return prettyPrint;
	}

	protected <U> void marshall(final U obj)
			throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException,
			IOException, NotImplementedSerializeException, MarshallExeption {
		if (obj != null) {
			final FakeChamp fieldsInfo = new FakeChamp(null, Object.class, TypeRelation.COMPOSITION, null);
			marshall(obj, fieldsInfo);
			while (!aFaire.isEmpty())
				deserialisePile();
		}
		writer.flush(); // le writer de l'appelant peut avoir été bufferisé
	}

	protected void write(final char c) throws IOException {
		writer.write(c);
	}

	protected void write(final String string) throws IOException {
		writer.write(string);
	}
}
