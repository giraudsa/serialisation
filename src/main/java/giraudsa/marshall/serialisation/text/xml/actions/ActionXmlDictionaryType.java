package giraudsa.marshall.serialisation.text.xml.actions;

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
	protected void ecritValeur(Marshaller marshaller, Map obj, FieldInformations fieldInformations) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException, IOException{
		Type[] types = fieldInformations.getParametreType();
		Type genericTypeKey = Object.class;
		Type genericTypeValue = Object.class;
		if(types != null && types.length > 1){
			genericTypeKey = types[0];
			genericTypeValue = types[1];
		}
		FakeChamp fakeChampKey = new FakeChamp(genericTypeKey.getClass().getSimpleName(), genericTypeKey, fieldInformations.getRelation());
		FakeChamp fakeChampValue = new FakeChamp(genericTypeValue.getClass().getSimpleName(), genericTypeValue, fieldInformations.getRelation());
		
		
		Map<?,?> map = (Map<?,?>) obj;
		Deque<Comportement> tmp = new ArrayDeque<>();
		for (Entry<?, ?> entry : map.entrySet()) {
			tmp.push(traiteChamp(marshaller, entry.getKey(), fakeChampKey));
			tmp.push(traiteChamp(marshaller, entry.getValue(), fakeChampValue));
		}
		pushComportements(marshaller, tmp);
	}
}
