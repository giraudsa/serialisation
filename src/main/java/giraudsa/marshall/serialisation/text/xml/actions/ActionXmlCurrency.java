package giraudsa.marshall.serialisation.text.xml.actions;

import java.io.IOException;
import java.util.Currency;

import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.text.xml.ActionXml;
import utils.champ.FieldInformations;

public class ActionXmlCurrency extends ActionXml<Currency> {

	public ActionXmlCurrency() {
		super();
	}

	@Override
	protected void ecritValeur(final Marshaller marshaller, final Currency currency,
			final FieldInformations fieldInformations, final boolean serialiseTout) throws IOException {
		writeEscape(marshaller, currency.getCurrencyCode());
	}
}
