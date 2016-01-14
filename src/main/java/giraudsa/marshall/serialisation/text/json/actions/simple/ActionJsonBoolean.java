package giraudsa.marshall.serialisation.text.json.actions.simple;

import java.io.IOException;

import giraudsa.marshall.serialisation.text.json.JsonMarshaller;
import giraudsa.marshall.serialisation.text.json.actions.ActionJsonSimpleWithoutQuote;



public class ActionJsonBoolean extends ActionJsonSimpleWithoutQuote<Boolean> {

	public ActionJsonBoolean(JsonMarshaller b) {
		super(b);
	}
	
	@Override
	protected boolean commenceObject(Boolean obj, boolean notApplicableHere) throws IOException {
		return false;
	}
	
	@Override
	protected void clotureObject(Boolean obj, boolean notApplicableHere) throws IOException {
		//pas d'accolade fermante pour un booleen
	}
}
