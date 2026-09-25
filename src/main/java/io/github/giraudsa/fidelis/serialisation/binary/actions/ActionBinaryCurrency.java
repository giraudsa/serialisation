package io.github.giraudsa.fidelis.serialisation.binary.actions;

import java.io.IOException;
import java.util.Currency;

import io.github.giraudsa.fidelis.serialisation.Marshaller;
import io.github.giraudsa.fidelis.serialisation.binary.ActionBinary;
import io.github.giraudsa.fidelis.utils.champ.FieldInformations;

public class ActionBinaryCurrency extends ActionBinary<Currency> {

	public ActionBinaryCurrency() {
		super();
	}

	@Override
	protected void ecritValeur(final Marshaller marshaller, final Currency currency,
			final FieldInformations fieldInformations, final boolean isDejaVu) throws IOException {
		if (!isDejaVu) {
			setDejaTotalementSerialise(marshaller, currency);
			writeUTF(marshaller, currency.getCurrencyCode());
		}
	}
}