package giraudsa.marshall.serialisation.text.json.actions;

import java.io.IOException;

import giraudsa.marshall.serialisation.Marshaller;
import utils.champ.FieldInformations;

public class ActionJsonSimpleWithQuote<T> extends ActionJsonSimple<T> {

	public ActionJsonSimpleWithQuote() {
		super();
	}
	
	@Override
	protected void ecritValeur(Marshaller marshaller, T obj, FieldInformations fieldInformations, boolean ecrisSeparateur) throws IOException{
		writeWithQuote(marshaller, getAEcrire(marshaller, obj));
	}

	protected String getAEcrire(Marshaller marshaller, T obj) {
		return obj.toString();
	}
}
