package io.github.giraudsa.fidelis.serialisation.text.json.actions.simple;

import java.io.IOException;

import io.github.giraudsa.fidelis.serialisation.Marshaller;
import io.github.giraudsa.fidelis.serialisation.text.json.actions.ActionJsonSimpleWithoutQuote;
import io.github.giraudsa.fidelis.utils.champ.FieldInformations;

public class ActionJsonVoid extends ActionJsonSimpleWithoutQuote<Void> {

	public ActionJsonVoid() {
		super();
	}

	@Override
	protected void clotureObject(final Marshaller marshaller, final Void obj, final boolean notApplicableHere)
			throws IOException {
		// pas d'accolade fermante pour un objet null
	}

	@Override
	protected boolean commenceObject(final Marshaller marshaller, final Void obj, final boolean notApplicableHere)
			throws IOException {
		return false;
	}

	@Override
	protected void ecritValeur(final Marshaller marshaller, final Void obj, final FieldInformations fieldInformations,
			final boolean ecrisSeparateur) throws IOException {
		write(marshaller, "null");
	}
}
