package giraudsa.marshall.serialisation.text;

import java.io.IOException;
import java.io.StringReader;
import java.text.DateFormat;
import java.util.Map;

import giraudsa.marshall.serialisation.ActionAbstrait;
import giraudsa.marshall.serialisation.Marshaller;
import utils.champ.FieldInformations;

public abstract class ActionText<T> extends ActionAbstrait<T> {
	protected ActionText() {
		super();
	}

	protected DateFormat getDateFormat(final Marshaller marshaller) {
		return getTextMarshaller(marshaller).df;
	}

	protected abstract Map<Character, String> getRemplacementChar();

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
		if (toBeEscaped.isEmpty())
			write(marshaller, toBeEscaped);
		final StringReader sr = new StringReader(toBeEscaped);
		int i = sr.read();
		while (i != -1) {
			final String r = getRemplacementChar().get((char) i);
			if (r == null)
				write(marshaller, (char) i);
			else
				write(marshaller, r);
			i = sr.read();
		}
	}

}
