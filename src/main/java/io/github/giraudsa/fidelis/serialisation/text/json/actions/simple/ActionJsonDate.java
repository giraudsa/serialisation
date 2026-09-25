package io.github.giraudsa.fidelis.serialisation.text.json.actions.simple;

import java.io.IOException;
import java.util.Date;

import io.github.giraudsa.fidelis.serialisation.Marshaller;
import io.github.giraudsa.fidelis.serialisation.text.json.actions.ActionJsonSimpleWithQuote;
import io.github.giraudsa.fidelis.utils.champ.FieldInformations;

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
