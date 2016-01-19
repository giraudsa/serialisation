package giraudsa.marshall.serialisation.binary.actions;

import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.binary.ActionBinary;

import utils.champ.FieldInformations;

import java.io.IOException;
import java.util.Currency;

public class ActionBinaryCurrency extends ActionBinary<Currency> {

	public ActionBinaryCurrency() {
		super();
	}
	@Override
	protected void ecritValeur(Marshaller marshaller, Currency currency, FieldInformations fieldInformations) throws IOException {
		if(!isDejaTotalementSerialise(marshaller, currency)){
			setDejaTotalementSerialise(marshaller, currency);
			writeUTF(marshaller, currency.getCurrencyCode());
		}
	}
}