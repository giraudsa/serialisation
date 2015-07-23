package giraudsa.marshall.serialisation.text.xml;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.text.ActionText;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class ActionXml<T> extends ActionText<T> {
	protected XmlMarshaller getXmlMarshaller(){
		return (XmlMarshaller)marshaller;
	}
	protected String balise;
	
	public ActionXml(Class<T> type, XmlMarshaller xmlMarshaller, String nomBalise){
		super(type, xmlMarshaller);
		balise = nomBalise;
		if(nomBalise == null) balise = type.getSimpleName();
	}
	
	@Override
	public void marshall(T obj, TypeRelation relation) throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, NotImplementedSerializeException {
		getXmlMarshaller().openTag(balise, getType());
		ecritValeur(obj, relation);
		getXmlMarshaller().closeTag(balise);
	}
}
