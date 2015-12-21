package giraudsa.marshall.serialisation.text.json.actions;

import java.io.IOException;
import giraudsa.marshall.serialisation.text.json.ActionJson;
import giraudsa.marshall.serialisation.text.json.JsonMarshaller;
import utils.Constants;

public abstract class ActionJsonSimple<T> extends ActionJson<T> {

	public ActionJsonSimple(JsonMarshaller b) {
		super(b);
	}
	
	@Override
	protected boolean ouvreAccolade(T obj, boolean typeDevinable) throws IOException {
		if(!typeDevinable){
			write("{");
			ecritType(obj);
			writeSeparator();
			ecritClef(Constants.VALEUR);
			return false;
		}
		return true;
	}
	
	@Override
	protected void fermeAccolade(T obj, boolean typeDevinable) throws IOException {
		if(!typeDevinable){
			write("}");
		}
	}
}
