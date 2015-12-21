package giraudsa.marshall.serialisation.text.xml.actions;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.text.xml.ActionXml;
import giraudsa.marshall.serialisation.text.xml.XmlMarshaller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Stack;
import java.util.Map.Entry;

@SuppressWarnings("rawtypes")
public class ActionXmlDictionaryType extends ActionXml<Map> {
	
	public ActionXmlDictionaryType(XmlMarshaller xmlM) {
		super(xmlM);
	}
	@Override
	protected void ecritValeur(Map obj, TypeRelation relation) throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException {
		Map<?,?> map = (Map<?,?>) obj;
		Stack<Comportement> tmp = new Stack<>();
		for (Entry<?, ?> entry : map.entrySet()) {
			tmp.push(new ComportementMarshallValue(entry.getKey(), "K", relation, false));
			tmp.push(new ComportementMarshallValue(entry.getValue(), "V", relation, false));
		}
		pushComportements(tmp);
	}
}
