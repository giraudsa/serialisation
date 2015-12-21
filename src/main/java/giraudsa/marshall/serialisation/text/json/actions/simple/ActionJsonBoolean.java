package giraudsa.marshall.serialisation.text.json.actions.simple;

import java.io.IOException;

import giraudsa.marshall.serialisation.text.json.JsonMarshaller;
import giraudsa.marshall.serialisation.text.json.actions.ActionJsonSimpleWithoutQuote;



public class ActionJsonBoolean extends ActionJsonSimpleWithoutQuote<Boolean> {

	public ActionJsonBoolean(JsonMarshaller b) {
		super(b);
	}
	
	@Override
	protected boolean ouvreAccolade(Boolean obj, boolean notApplicableHere) throws IOException {
		return false;
	}
	
	@Override
	protected void fermeAccolade(Boolean obj, boolean notApplicableHere) throws IOException {
	}
}
