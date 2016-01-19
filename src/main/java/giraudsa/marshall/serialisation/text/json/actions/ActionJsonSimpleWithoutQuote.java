package giraudsa.marshall.serialisation.text.json.actions;

import java.io.IOException;

import giraudsa.marshall.serialisation.Marshaller;
import utils.champ.FieldInformations;

public class ActionJsonSimpleWithoutQuote<T> extends ActionJsonSimple<T> {

	public ActionJsonSimpleWithoutQuote() {
		super();
	}
	
	@Override
	protected void ecritValeur(Marshaller marshaller, T obj, FieldInformations fieldInformations, boolean ecrisSeparateur) throws IOException{
		writeEscape(marshaller, obj.toString());
	}
}
