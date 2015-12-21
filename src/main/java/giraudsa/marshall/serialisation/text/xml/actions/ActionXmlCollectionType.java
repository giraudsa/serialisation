package giraudsa.marshall.serialisation.text.xml.actions;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.text.xml.ActionXml;
import giraudsa.marshall.serialisation.text.xml.XmlMarshaller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;

@SuppressWarnings("rawtypes")
public class ActionXmlCollectionType extends ActionXml<Collection> {
	
	@Override
	protected Class<?> getType(Collection obj) {
		return (obj.getClass().getName().toLowerCase().indexOf("hibernate") != -1) ? ArrayList.class : obj.getClass();
	}

	public ActionXmlCollectionType(XmlMarshaller xmlM) {
		super(xmlM);
	}

	@Override
	protected void ecritValeur(Collection obj, TypeRelation relation) throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException {
		Stack<Comportement> tmp = new Stack<>();
		Collection<?> collection = (Collection<?>) obj;
		for (Object value : collection) {
			tmp.push(new ComportementMarshallValue(value, "V", relation, false));
		}
		pushComportements(tmp);
	}

}
