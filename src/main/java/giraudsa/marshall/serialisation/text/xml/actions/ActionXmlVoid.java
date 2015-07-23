package giraudsa.marshall.serialisation.text.xml.actions;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.text.xml.ActionXml;
import giraudsa.marshall.serialisation.text.xml.XmlMarshaller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class ActionXmlVoid<T> extends ActionXml<T> {

	public ActionXmlVoid(Class<T> type, XmlMarshaller xmlM, String nomBalise) {
		super(type, xmlM, nomBalise);
		if(nomBalise == null) balise = "vide";
	}

	@Override
	protected void ecritValeur(T obj, TypeRelation relation) throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException {
		write("null");
	}
	
}
