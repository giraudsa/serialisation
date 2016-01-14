package giraudsa.marshall.serialisation.text.json.actions.simple;

import giraudsa.marshall.serialisation.text.json.JsonMarshaller;
import giraudsa.marshall.serialisation.text.json.actions.ActionJsonSimpleWithQuote;

import java.io.IOException;

public class ActionJsonString extends ActionJsonSimpleWithQuote<String> {

	public ActionJsonString(JsonMarshaller jsonM) {
		super(jsonM);
	}
	
	@Override
	protected boolean commenceObject(String obj, boolean notApplicableHere) throws IOException {
		return false;
	}
	
	@Override
	protected void clotureObject(String obj, boolean notApplicableHere) throws IOException {
		//pas d'accolade fermante pour un objet simple
	}


}
