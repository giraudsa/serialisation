package giraudsa.marshall.serialisation.text.xml.actions;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.text.xml.ActionXml;
import giraudsa.marshall.serialisation.text.xml.XmlMarshaller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Map.Entry;

@SuppressWarnings("rawtypes")
public class ActionXmlDictionaryType<T extends Map> extends ActionXml<T> {
	
	@Override
	protected Class<?> getType() {
		return type;
	}

	public ActionXmlDictionaryType(Class<T> type, XmlMarshaller xmlM, String nomBalise) {
		super(type, xmlM, nomBalise);
		if(nomBalise == null) balise = "Dico";
	}
	@Override
	protected void ecritValeur(T obj, TypeRelation relation) throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException {
		Map<?,?> map = (Map<?,?>) obj;
		for (Entry<?, ?> entry : map.entrySet()) {
			marshallValue(entry.getKey(), "K", relation);
			marshallValue(entry.getValue(), "V", relation);
		}
	}
}
