package giraudsa.marshall.serialisation.text.json.actions.simple;

import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.text.json.actions.ActionJsonSimpleWithQuote;

import java.io.IOException;

public class ActionJsonString extends ActionJsonSimpleWithQuote<String> {

	public ActionJsonString() {
		super();
	}
	
	@Override
	protected boolean commenceObject(Marshaller marshaller, String obj, boolean notApplicableHere) throws IOException {
		return false;
	}
	
	@Override
	protected void clotureObject(Marshaller marshaller, String obj, boolean notApplicableHere) throws IOException {
		//pas d'accolade fermante pour un objet simple
	}


}
