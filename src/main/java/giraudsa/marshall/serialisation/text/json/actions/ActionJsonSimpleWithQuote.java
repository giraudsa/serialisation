package giraudsa.marshall.serialisation.text.json.actions;

import java.io.IOException;

import giraudsa.marshall.serialisation.Marshaller;
import utils.champ.FieldInformations;

public class ActionJsonSimpleWithQuote<T> extends ActionJsonSimple<T> {

	public ActionJsonSimpleWithQuote() {
		super();
	}

	@Override
	protected void ecritValeur(final Marshaller marshaller, final T obj, final FieldInformations fieldInformations,
			final boolean ecrisSeparateur) throws IOException {
		writeWithQuote(marshaller, getAEcrire(marshaller, obj));
	}

	protected String getAEcrire(final Marshaller marshaller, final T obj) {
		return obj.toString();
	}
}
