package giraudsa.marshall.serialisation.text.json.actions.simple;

import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.text.json.actions.ActionJsonSimpleWithQuote;
import java.util.Currency;

public class ActionJsonCurrency extends ActionJsonSimpleWithQuote<Currency> {

	public ActionJsonCurrency() {
		super();
	}

	@Override
	protected String getAEcrire(Marshaller marshaller, Currency currency) {
		return currency.getCurrencyCode();
	}
}
