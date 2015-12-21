package giraudsa.marshall.serialisation.text.xml.actions;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.text.xml.ActionXml;
import giraudsa.marshall.serialisation.text.xml.XmlMarshaller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class ActionXmlVoid extends ActionXml<Void> {

	public ActionXmlVoid(XmlMarshaller xmlM) {
		super(xmlM);
	}

	@Override
	protected void ecritValeur(Void obj, TypeRelation relation)
			throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException {
		write("null");
	}
	
}
