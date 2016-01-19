package giraudsa.marshall.serialisation.text.json.actions.simple;

import java.io.IOException;

import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.text.json.actions.ActionJsonSimpleWithoutQuote;

public class ActionJsonInteger  extends ActionJsonSimpleWithoutQuote<Integer>{

	public ActionJsonInteger() {
		super();
	}
	
	@Override
	protected boolean commenceObject(Marshaller marshaller, Integer obj, boolean notApplicableHere) throws IOException {
		return false;
	}
	
	@Override
	protected void clotureObject(Marshaller marshaller, Integer obj, boolean notApplicableHere) throws IOException {
		//pas d'accolade fermante pour un entier
	}

}
