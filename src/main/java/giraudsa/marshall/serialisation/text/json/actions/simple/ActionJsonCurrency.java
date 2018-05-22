package giraudsa.marshall.serialisation.text.json.actions.simple;

import java.util.Currency;

import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.text.json.actions.ActionJsonSimpleWithQuote;

public class ActionJsonCurrency extends ActionJsonSimpleWithQuote<Currency> {

	public ActionJsonCurrency() {
		super();
	}

	@Override
	protected String getAEcrire(final Marshaller marshaller, final Currency currency) {
		return currency.getCurrencyCode();
	}
}
