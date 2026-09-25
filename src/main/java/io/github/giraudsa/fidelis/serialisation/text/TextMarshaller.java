package io.github.giraudsa.fidelis.serialisation.text;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import io.github.giraudsa.fidelis.annotations.TypeRelation;
import io.github.giraudsa.fidelis.exception.MarshallExeption;
import io.github.giraudsa.fidelis.exception.NotImplementedSerializeException;
import io.github.giraudsa.fidelis.serialisation.Marshaller;
import io.github.giraudsa.fidelis.strategie.StrategieDeSerialisation;
import io.github.giraudsa.fidelis.utils.ConfigurationMarshalling;
import io.github.giraudsa.fidelis.utils.CopieFormatDate;
import io.github.giraudsa.fidelis.utils.EntityManager;
import io.github.giraudsa.fidelis.utils.champ.FakeChamp;
import io.github.giraudsa.fidelis.utils.io.DatesIso;
import io.github.giraudsa.fidelis.utils.io.SortieTexte;

public abstract class TextMarshaller extends Marshaller {
	/** Sortie tamponnée non synchronisée (vidée vers le Writer de l'appelant par flush, en fin de sérialisation). */
	private static SortieTexte bufferise(final Writer writer) {
		if (writer instanceof SortieTexte)
			return (SortieTexte) writer;
		return new SortieTexte(writer);
	}

	/** format de date de la configuration ; sa copie privée n'est faite qu'à la première date écrite avec. */
	private final SimpleDateFormat formatSource;
	private DateFormat df;
	/** le format de date est le format ISO UTC par défaut : les dates peuvent être écrites sans lui (DatesIso). */
	protected final boolean dateIsoUtc;
	protected final boolean isUniversalId;
	private final boolean prettyPrint;

	protected final SortieTexte writer;

	protected TextMarshaller(final Writer writer, final SimpleDateFormat dateFormat,
			final StrategieDeSerialisation strategie, final EntityManager entityManager) {
		super(strategie, entityManager);
		this.writer = bufferise(writer);
		formatSource = dateFormat;
		dateIsoUtc = CopieFormatDate.estIsoUtc(dateFormat);
		isUniversalId = ConfigurationMarshalling.getEstIdUniversel();
		prettyPrint = ConfigurationMarshalling.isPrettyPrint();
	}

	/** @return la copie privée du format de date (SimpleDateFormat n'est pas thread-safe). */
	protected DateFormat getDateFormat() {
		if (df == null)
			df = CopieFormatDate.copie(formatSource);
		return df;
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
		try {
			if (obj != null) {
				final FakeChamp fieldsInfo = new FakeChamp(null, Object.class, TypeRelation.COMPOSITION, null);
				marshall(obj, fieldsInfo);
				while (!aFaire.isEmpty())
					deserialisePile();
			}
			writer.flush(); // le writer de l'appelant est derrière un tampon
		} finally {
			rendTables();
		}
	}

	protected void write(final char c) throws IOException {
		writer.write(c);
	}

	protected void write(final String string) throws IOException {
		writer.write(string);
	}
}
