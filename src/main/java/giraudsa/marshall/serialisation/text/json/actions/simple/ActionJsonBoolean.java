package giraudsa.marshall.serialisation.text.json.actions.simple;

import java.io.IOException;

import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.text.json.actions.ActionJsonSimpleWithoutQuote;



public class ActionJsonBoolean extends ActionJsonSimpleWithoutQuote<Boolean> {

	public ActionJsonBoolean() {
		super();
	}
	
	@Override
	protected boolean commenceObject(Marshaller marshaller, Boolean obj, boolean notApplicableHere) throws IOException {
		return false;
	}
	
	@Override
	protected void clotureObject(Marshaller marshaller, Boolean obj, boolean notApplicableHere) throws IOException {
		//pas d'accolade fermante pour un booleen
	}
}
