package io.github.giraudsa.fidelis.serialisation.binary.actions.simple;

import java.io.IOException;
import java.util.Locale;

import io.github.giraudsa.fidelis.serialisation.Marshaller;
import io.github.giraudsa.fidelis.serialisation.binary.ActionBinary;
import io.github.giraudsa.fidelis.utils.champ.FieldInformations;

public class ActionBinaryLocale extends ActionBinary<Locale> {

	@Override
	protected void ecritValeur(final Marshaller marshaller, final Locale locale,
			final FieldInformations fieldInformation, final boolean isDejaVu) throws IOException {
		if (!isDejaVu) {
			setDejaTotalementSerialise(marshaller, locale);
			writeUTF(marshaller, locale.toString());
		}
	}

}
