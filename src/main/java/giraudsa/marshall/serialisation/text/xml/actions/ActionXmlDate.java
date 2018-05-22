package giraudsa.marshall.serialisation.text.xml.actions;

import java.io.IOException;
import java.util.Date;

import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.text.xml.ActionXml;
import utils.champ.FieldInformations;

public class ActionXmlDate extends ActionXml<Date> {

	public ActionXmlDate() {
		super();
	}

	@Override
	protected void ecritValeur(final Marshaller marshaller, final Date date, final FieldInformations f,
			final boolean serialiseTout) throws IOException {
		write(marshaller, getDateFormat(marshaller).format(date));
	}
}
