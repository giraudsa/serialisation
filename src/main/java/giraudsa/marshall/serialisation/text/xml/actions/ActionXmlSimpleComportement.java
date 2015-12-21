package giraudsa.marshall.serialisation.text.xml.actions;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.text.xml.ActionXml;
import giraudsa.marshall.serialisation.text.xml.XmlMarshaller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang3.StringEscapeUtils;

public class ActionXmlSimpleComportement<T> extends ActionXml<T> {

	public ActionXmlSimpleComportement(XmlMarshaller xmlM) {
		super(xmlM);
	}

	@Override
	protected void ecritValeur(Object obj, TypeRelation relation) throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException {
		write(StringEscapeUtils.escapeXml10(obj.toString()));
	}
}
