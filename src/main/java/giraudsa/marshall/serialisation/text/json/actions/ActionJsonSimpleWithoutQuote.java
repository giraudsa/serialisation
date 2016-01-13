package giraudsa.marshall.serialisation.text.json.actions;

import java.io.IOException;
import org.apache.commons.lang3.StringEscapeUtils;

import giraudsa.marshall.serialisation.text.json.JsonMarshaller;
import utils.champ.FieldInformations;

public class ActionJsonSimpleWithoutQuote<T> extends ActionJsonSimple<T> {

	public ActionJsonSimpleWithoutQuote(JsonMarshaller b) {
		super(b);
	}
	
	@Override
	protected void ecritValeur(T obj, FieldInformations fieldInformations, boolean ecrisSeparateur) throws IOException{
		write(StringEscapeUtils.escapeJson(obj.toString()));
	}
}
