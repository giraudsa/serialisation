package giraudsa.marshall.serialisation.text.xml.actions;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.text.xml.ActionXml;
import giraudsa.marshall.serialisation.text.xml.XmlMarshaller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

@SuppressWarnings("rawtypes")
public class ActionXmlCollectionType<T extends Collection> extends ActionXml<T> {

	
	private Class<?> _type = null;
	@Override
	protected Class<?> getType() {
		if(_type != null) return _type;
		if(type.getName().toLowerCase().indexOf("hibernate") != -1) _type = ArrayList.class;
		else _type = type;
		return _type;
	}
	public ActionXmlCollectionType(Class<T> type, XmlMarshaller xmlM, String nomBalise) {
		super(type, xmlM, nomBalise);
		if(nomBalise == null) balise = "liste";
	}

	@Override
	protected void ecritValeur(T obj, TypeRelation relation) throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException {
		Collection<?> collection = (Collection<?>) obj;
		for (Object value : collection) {
			marshallValue(value, "V", relation);
		}
	}

}
