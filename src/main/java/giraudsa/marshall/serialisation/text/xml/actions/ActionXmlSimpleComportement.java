package giraudsa.marshall.serialisation.text.xml.actions;

import giraudsa.marshall.serialisation.text.xml.ActionXml;
import giraudsa.marshall.serialisation.text.xml.XmlMarshaller;
import utils.champ.FieldInformations;

import java.io.IOException;
import org.apache.commons.lang3.StringEscapeUtils;

public class ActionXmlSimpleComportement<T> extends ActionXml<T> {

	public ActionXmlSimpleComportement(XmlMarshaller xmlM) {
		super(xmlM);
	}

	@Override
	protected void ecritValeur(Object obj, FieldInformations fieldInformations) throws IOException{
		write(StringEscapeUtils.escapeXml10(obj.toString()));
	}
}
