package giraudsa.marshall.serialisation.text.xml.actions;

import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.text.xml.ActionXml;
import utils.champ.FieldInformations;

import java.io.IOException;
import java.net.InetAddress;

public class ActionXmlInetAdress extends ActionXml<InetAddress> {

	public ActionXmlInetAdress() {
		super();
	}

	@Override
	protected void ecritValeur(Marshaller marshaller, InetAddress inetAdress, FieldInformations fieldInformations, boolean serialiseTout) throws IOException{
		writeEscape(marshaller, inetAdress.getHostAddress());
	}
}
