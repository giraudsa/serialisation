package giraudsa.marshall.serialisation.text.xml.actions;

import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.text.xml.ActionXml;
import utils.champ.FieldInformations;

import java.io.IOException;
import java.util.Currency;

public class ActionXmlCurrency extends ActionXml<Currency> {

	public ActionXmlCurrency() {
		super();
	}

	@Override
	protected void ecritValeur(Marshaller marshaller, Currency currency, FieldInformations fieldInformations) throws IOException{
		writeEscape(marshaller, currency.getCurrencyCode());
	}
}
