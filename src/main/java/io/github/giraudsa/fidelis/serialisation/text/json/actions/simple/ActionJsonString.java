package io.github.giraudsa.fidelis.serialisation.text.json.actions.simple;

import java.io.IOException;

import io.github.giraudsa.fidelis.serialisation.Marshaller;
import io.github.giraudsa.fidelis.serialisation.text.json.actions.ActionJsonSimpleWithQuote;

public class ActionJsonString extends ActionJsonSimpleWithQuote<String> {

	public ActionJsonString() {
		super();
	}

	@Override
	protected void clotureObject(final Marshaller marshaller, final String obj, final boolean notApplicableHere)
			throws IOException {
		// pas d'accolade fermante pour un objet simple
	}

	@Override
	protected boolean commenceObject(final Marshaller marshaller, final String obj, final boolean notApplicableHere)
			throws IOException {
		return false;
	}

}
