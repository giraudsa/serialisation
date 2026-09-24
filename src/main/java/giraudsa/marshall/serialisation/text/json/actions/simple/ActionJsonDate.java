package giraudsa.marshall.serialisation.text.json.actions.simple;

import java.io.IOException;
import java.util.Date;

import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.text.json.actions.ActionJsonSimpleWithQuote;
import utils.champ.FieldInformations;

public class ActionJsonDate extends ActionJsonSimpleWithQuote<Date> {

	public ActionJsonDate() {
		super();
	}

	@Override
	protected void ecritValeur(final Marshaller marshaller, final Date obj, final FieldInformations fieldInformations,
			final boolean ecrisSeparateur) throws IOException {
		if (!ecritDateRapide(marshaller, obj.getTime()))
			super.ecritValeur(marshaller, obj, fieldInformations, ecrisSeparateur);
	}

	@Override
	protected String getAEcrire(final Marshaller marshaller, final Date obj) {
		return getDateFormat(marshaller).format(obj);
	}
}
