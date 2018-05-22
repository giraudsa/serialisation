package giraudsa.marshall.serialisation.text.xml.actions;

import java.io.IOException;

import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.text.xml.ActionXml;
import utils.champ.FieldInformations;

public class ActionXmlVoid extends ActionXml<Void> {

	public ActionXmlVoid() {
		super();
	}

	@Override
	protected void ecritValeur(final Marshaller marshaller, final Void obj, final FieldInformations fieldInformations,
			final boolean serialiseTout) throws IOException {
		write(marshaller, "null");
	}

}
