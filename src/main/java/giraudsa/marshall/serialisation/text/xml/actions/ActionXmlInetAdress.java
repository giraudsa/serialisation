package giraudsa.marshall.serialisation.text.xml.actions;

import java.io.IOException;
import java.net.InetAddress;

import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.text.xml.ActionXml;
import utils.champ.FieldInformations;

public class ActionXmlInetAdress extends ActionXml<InetAddress> {

	public ActionXmlInetAdress() {
		super();
	}

	@Override
	protected void ecritValeur(final Marshaller marshaller, final InetAddress inetAdress,
			final FieldInformations fieldInformations, final boolean serialiseTout) throws IOException {
		writeEscape(marshaller, inetAdress.getHostAddress());
	}
}
