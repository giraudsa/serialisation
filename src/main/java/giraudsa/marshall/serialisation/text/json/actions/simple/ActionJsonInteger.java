package giraudsa.marshall.serialisation.text.json.actions.simple;

import java.io.IOException;

import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.text.json.actions.ActionJsonSimpleWithoutQuote;

public class ActionJsonInteger extends ActionJsonSimpleWithoutQuote<Integer> {

	public ActionJsonInteger() {
		super();
	}

	@Override
	protected void clotureObject(final Marshaller marshaller, final Integer obj, final boolean notApplicableHere)
			throws IOException {
		// pas d'accolade fermante pour un entier
	}

	@Override
	protected boolean commenceObject(final Marshaller marshaller, final Integer obj, final boolean notApplicableHere)
			throws IOException {
		return false;
	}

}
