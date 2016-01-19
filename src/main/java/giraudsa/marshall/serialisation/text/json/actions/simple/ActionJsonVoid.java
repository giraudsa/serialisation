package giraudsa.marshall.serialisation.text.json.actions.simple;

import java.io.IOException;

import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.text.json.actions.ActionJsonSimpleWithoutQuote;
import utils.champ.FieldInformations;

public class ActionJsonVoid extends ActionJsonSimpleWithoutQuote<Void> {

	public ActionJsonVoid() {
		super();
	}
	
	@Override protected void ecritValeur(Marshaller marshaller, Void obj, FieldInformations fieldInformations, boolean ecrisSeparateur) throws IOException{
		write(marshaller, "null");
	}
	
	@Override
	protected boolean commenceObject(Marshaller marshaller, Void obj, boolean notApplicableHere) throws IOException {
		return false;
	}
	
	@Override
	protected void clotureObject(Marshaller marshaller, Void obj, boolean notApplicableHere) throws IOException {
		//pas d'accolade fermante pour un objet null
	}
}
