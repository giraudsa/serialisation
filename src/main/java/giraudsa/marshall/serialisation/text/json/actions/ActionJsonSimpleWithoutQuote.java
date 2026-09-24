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
		// un nombre (ou booléen atomique) n'a aucun caractère à échapper
		if (obj instanceof Integer || obj instanceof Long || obj instanceof Short || obj instanceof Byte)
			ecritEntier(marshaller, ((Number) obj).longValue());
		else if (obj instanceof Number)
			ecritBrut(marshaller, obj.toString());
		else
			writeEscape(marshaller, obj.toString());
	}
}
