package giraudsa.marshall.serialisation.text.json.actions;

import java.io.IOException;
import giraudsa.marshall.serialisation.text.json.ActionJson;
import giraudsa.marshall.serialisation.text.json.JsonMarshaller;
import utils.Constants;

public abstract class ActionJsonSimple<T> extends ActionJson<T> {

	protected ActionJsonSimple(JsonMarshaller b) {
		super(b);
	}
	
	@Override
	protected boolean commenceObject(T obj, boolean typeDevinable) throws IOException {
		if(!typeDevinable){
			ouvreAccolade();
			ecritType(obj);
			writeSeparator();
			ecritClef(Constants.VALEUR);
			return false;
		}
		return true;
	}
	
	@Override
	protected void clotureObject(T obj, boolean typeDevinable) throws IOException {
		if(!typeDevinable){
			fermeAccolade();
		}
	}
}
