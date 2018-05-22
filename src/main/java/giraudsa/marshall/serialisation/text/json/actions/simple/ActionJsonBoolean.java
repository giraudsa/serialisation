package giraudsa.marshall.serialisation.text.json.actions.simple;

import java.io.IOException;

import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.text.json.actions.ActionJsonSimpleWithoutQuote;

public class ActionJsonBoolean extends ActionJsonSimpleWithoutQuote<Boolean> {

	public ActionJsonBoolean() {
		super();
	}

	@Override
	protected void clotureObject(final Marshaller marshaller, final Boolean obj, final boolean notApplicableHere)
			throws IOException {
		// pas d'accolade fermante pour un booleen
	}

	@Override
	protected boolean commenceObject(final Marshaller marshaller, final Boolean obj, final boolean notApplicableHere)
			throws IOException {
		return false;
	}
}
