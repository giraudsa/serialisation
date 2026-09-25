package io.github.giraudsa.fidelis.serialisation.text;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Map;

import io.github.giraudsa.fidelis.serialisation.ActionAbstrait;
import io.github.giraudsa.fidelis.serialisation.Marshaller;
import io.github.giraudsa.fidelis.utils.champ.FieldInformations;

public abstract class ActionText<T> extends ActionAbstrait<T> {
	protected ActionText() {
		super();
	}

	protected DateFormat getDateFormat(final Marshaller marshaller) {
		return getTextMarshaller(marshaller).getDateFormat();
	}

	protected abstract Map<Character, String> getRemplacementChar();

	/**
	 * Table construite à partir de {@link #getRemplacementChar()}. Les classes
	 * dérivées la redéfinissent avec une instance statique pour ne pas la
	 * reconstruire à chaque écriture.
	 */
	protected TableEchappement getTableEchappement() {
		return new TableEchappement(getRemplacementChar());
	}

	private TextMarshaller getTextMarshaller(final Marshaller marshaller) {
		return (TextMarshaller) marshaller;
	}

	@Override
	protected boolean isUniversalId(final Marshaller marshaller) {
		return getTextMarshaller(marshaller).isUniversalId;
	}

	protected boolean serialiseTout(final Marshaller marshaller, final Object obj,
			final FieldInformations fieldInformations) {
		return strategieSerialiseTout(marshaller, fieldInformations) && !isDejaTotalementSerialise(marshaller, obj);
	}

	protected void write(final Marshaller marshaller, final char s) throws IOException {
		getTextMarshaller(marshaller).write(s);
	}

	protected void write(final Marshaller marshaller, final String s) throws IOException {
		getTextMarshaller(marshaller).write(s);
	}

	protected void writeEscape(final Marshaller marshaller, final String toBeEscaped) throws IOException {
		getTableEchappement().ecris(getTextMarshaller(marshaller).writer, toBeEscaped);
	}

}
