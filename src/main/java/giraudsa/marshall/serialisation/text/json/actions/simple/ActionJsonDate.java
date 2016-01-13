package giraudsa.marshall.serialisation.text.json.actions.simple;

import giraudsa.marshall.serialisation.text.json.JsonMarshaller;
import giraudsa.marshall.serialisation.text.json.actions.ActionJsonSimpleWithQuote;
import utils.champ.FieldInformations;

import java.io.IOException;
import java.util.Date;

public class ActionJsonDate extends ActionJsonSimpleWithQuote<Date> {

	public ActionJsonDate(JsonMarshaller jsonM) {
		super(jsonM);
	}

	@Override
	protected void ecritValeur(Date obj, FieldInformations fieldInformations, boolean ecrisSeparateur) throws IOException{
		writeWithQuote(getDateFormat().format(obj));
	}
}
