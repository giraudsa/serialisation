package giraudsa.marshall.serialisation.text.json.actions;

import java.io.IOException;

import giraudsa.marshall.serialisation.Marshaller;
import utils.champ.FieldInformations;

public class ActionJsonSimpleWithoutQuote<T> extends ActionJsonSimple<T> {

	public ActionJsonSimpleWithoutQuote() {
		super();
	}

	@Override
	protected void ecritValeur(final Marshaller marshaller, final T obj, final FieldInformations fieldInformations,
			final boolean ecrisSeparateur) throws IOException {
		writeEscape(marshaller, obj.toString());
	}
}
