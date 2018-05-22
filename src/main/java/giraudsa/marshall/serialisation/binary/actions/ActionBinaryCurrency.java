package giraudsa.marshall.serialisation.binary.actions;

import java.io.IOException;
import java.util.Currency;

import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.binary.ActionBinary;
import utils.champ.FieldInformations;

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