package giraudsa.marshall.serialisation.text.json.actions.simple;

import java.io.IOException;
import giraudsa.marshall.serialisation.text.json.JsonMarshaller;
import giraudsa.marshall.serialisation.text.json.actions.ActionJsonSimpleWithoutQuote;
import utils.champ.FieldInformations;

public class ActionJsonVoid extends ActionJsonSimpleWithoutQuote<Void> {

	public ActionJsonVoid(JsonMarshaller b) {
		super(b);
	}
	
	@Override protected void ecritValeur(Void obj, FieldInformations fieldInformations, boolean ecrisSeparateur) throws IOException{
		write("null");
	}
	
	@Override
	protected boolean ouvreAccolade(Void obj, boolean notApplicableHere) throws IOException {
		return false;
	}
	
	@Override
	protected void fermeAccolade(Void obj, boolean notApplicableHere) throws IOException {
		//pas d'accolade fermante pour un objet null
	}
}
