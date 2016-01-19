package giraudsa.marshall.serialisation.text.json.actions;

import java.io.IOException;

import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.text.json.ActionJson;
import utils.Constants;

public abstract class ActionJsonSimple<T> extends ActionJson<T> {

	protected ActionJsonSimple() {
		super();
	}
	
	@Override
	protected boolean commenceObject(Marshaller marshaller, T obj, boolean typeDevinable) throws IOException {
		if(!typeDevinable){
			ouvreAccolade(marshaller);
			ecritType(marshaller, obj);
			writeSeparator(marshaller);
			ecritClef(marshaller, Constants.VALEUR);
			return false;
		}
		return true;
	}
	
	@Override
	protected void clotureObject(Marshaller marshaller, T obj, boolean typeDevinable) throws IOException {
		if(!typeDevinable){
			fermeAccolade(marshaller);
		}
	}
}
