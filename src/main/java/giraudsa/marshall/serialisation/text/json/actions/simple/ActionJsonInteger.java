package giraudsa.marshall.serialisation.text.json.actions.simple;

import java.io.IOException;

import giraudsa.marshall.serialisation.text.json.JsonMarshaller;
import giraudsa.marshall.serialisation.text.json.actions.ActionJsonSimpleWithoutQuote;

public class ActionJsonInteger  extends ActionJsonSimpleWithoutQuote<Integer>{

	public ActionJsonInteger(JsonMarshaller b) {
		super(b);
	}
	
	@Override
	protected boolean commenceObject(Integer obj, boolean notApplicableHere) throws IOException {
		return false;
	}
	
	@Override
	protected void clotureObject(Integer obj, boolean notApplicableHere) throws IOException {
		//pas d'accolade fermante pour un entier
	}

}
