package giraudsa.marshall.serialisation.text.xml.actions;

import giraudsa.marshall.exception.MarshallExeption;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.text.xml.ActionXml;
import utils.champ.FakeChamp;
import utils.champ.FieldInformations;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.Map.Entry;

@SuppressWarnings("rawtypes")
public class ActionXmlDictionaryType extends ActionXml<Map> {
	
	public ActionXmlDictionaryType() {
		super();
	}
	@Override
	protected void ecritValeur(Marshaller marshaller, Map obj, FieldInformations fieldInformations) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException, IOException, MarshallExeption{
		Type[] types = fieldInformations.getParametreType();
		Type genericTypeKey = Object.class;
		Type genericTypeValue = Object.class;
		if(types != null && types.length > 1){
			genericTypeKey = types[0];
			genericTypeValue = types[1];
		}
		String clefKey = genericTypeKey instanceof Class ? ((Class<?>)genericTypeKey).getSimpleName() : "Key";
		String clefValue = genericTypeValue instanceof Class ? ((Class<?>)genericTypeValue).getSimpleName() : "Value";
		FakeChamp fakeChampKey = new FakeChamp(clefKey, genericTypeKey, fieldInformations.getRelation());
		FakeChamp fakeChampValue = new FakeChamp(clefValue, genericTypeValue, fieldInformations.getRelation());
		
		
		Map<?,?> map = (Map<?,?>) obj;
		Deque<Comportement> tmp = new ArrayDeque<>();
		for (Entry<?, ?> entry : map.entrySet()) {
			tmp.push(traiteChamp(marshaller, entry.getKey(), fakeChampKey));
			tmp.push(traiteChamp(marshaller, entry.getValue(), fakeChampValue));
		}
		pushComportements(marshaller, tmp);
	}
}
