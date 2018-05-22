package giraudsa.marshall.serialisation.text.json.actions.simple;

import java.util.Date;

import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.text.json.actions.ActionJsonSimpleWithQuote;

public class ActionJsonDate extends ActionJsonSimpleWithQuote<Date> {

	public ActionJsonDate() {
		super();
	}

	@Override
	protected String getAEcrire(final Marshaller marshaller, final Date obj) {
		return getDateFormat(marshaller).format(obj);
	}
}
