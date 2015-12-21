package giraudsa.marshall.serialisation.text.json.actions.simple;

import java.io.IOException;

import giraudsa.marshall.serialisation.text.json.JsonMarshaller;
import giraudsa.marshall.serialisation.text.json.actions.ActionJsonSimpleWithoutQuote;

public class ActionJsonInteger  extends ActionJsonSimpleWithoutQuote<Integer>{

	public ActionJsonInteger(JsonMarshaller b) {
		super(b);
	}
	
	@Override
	protected boolean ouvreAccolade(Integer obj, boolean notApplicableHere) throws IOException {
		return false;
	}
	
	@Override
	protected void fermeAccolade(Integer obj, boolean notApplicableHere) throws IOException {
	}

}
