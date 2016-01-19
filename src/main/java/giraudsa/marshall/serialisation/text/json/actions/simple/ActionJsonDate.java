package giraudsa.marshall.serialisation.text.json.actions.simple;

import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.text.json.actions.ActionJsonSimpleWithQuote;
import java.util.Date;

public class ActionJsonDate extends ActionJsonSimpleWithQuote<Date> {

	public ActionJsonDate() {
		super();
	}
	@Override
	protected String getAEcrire(Marshaller marshaller, Date obj) {
		return getDateFormat(marshaller).format(obj);
	}
}
