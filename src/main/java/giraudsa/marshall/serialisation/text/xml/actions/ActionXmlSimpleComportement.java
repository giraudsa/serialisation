package giraudsa.marshall.serialisation.text.xml.actions;

import java.io.IOException;

import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.text.xml.ActionXml;
import utils.champ.FieldInformations;

public class ActionXmlSimpleComportement<T> extends ActionXml<T> {

	public ActionXmlSimpleComportement() {
		super();
	}

	@Override
	protected void ecritValeur(final Marshaller marshaller, final Object obj, final FieldInformations fieldInformations,
			final boolean serialiseTout) throws IOException {
		writeEscape(marshaller, obj.toString());
	}
}
