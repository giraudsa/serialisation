package giraudsa.marshall.serialisation.binary.actions;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.binary.ActionBinary;

import utils.champ.FakeChamp;
import utils.champ.FieldInformations;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.Stack;

@SuppressWarnings("rawtypes")
public class ActionBinaryDictionaryType extends ActionBinary<Map> {

	public ActionBinaryDictionaryType() {
		super();
	}

	@Override
	protected void ecritValeur(Marshaller marshaller, Map map, FieldInformations fieldInformations, boolean isDejaVu) throws IOException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException{
		Type[] types = fieldInformations.getParametreType();
		Type genericTypeKey = Object.class;
		Type genericTypeValue = Object.class;
		if(types != null && types.length > 1){
			genericTypeKey = types[0];
			genericTypeValue = types[1];
		}
		FakeChamp fakeChampKey = new FakeChamp("K", genericTypeKey, fieldInformations.getRelation());
		FakeChamp fakeChampValue = new FakeChamp("V", genericTypeValue, fieldInformations.getRelation());
		
		
		Stack<Comportement> tmp = new Stack<Comportement>();
		if(!isDejaVu){
			if(isCompleteMarshalling(marshaller) || fieldInformations.getRelation()==TypeRelation.COMPOSITION)
				setDejaTotalementSerialise(marshaller, map);
			writeInt(marshaller, map.size());
			for (Object entry : map.entrySet()) {
				tmp.push(traiteChamp(marshaller, ((Entry)entry).getKey(), fakeChampKey));
				tmp.push(traiteChamp(marshaller, ((Entry)entry).getValue(), fakeChampValue));
			}
		}else if(!isCompleteMarshalling(marshaller) && fieldInformations.getRelation() == TypeRelation.COMPOSITION){//deja vu, donc on passe ici qd la relation est de type COMPOSITION
			setDejaTotalementSerialise(marshaller, map);
			for(Object entry : map.entrySet()){
				tmp.push(traiteChamp(marshaller, ((Entry)entry).getKey(), fakeChampKey));
				tmp.push(traiteChamp(marshaller, ((Entry)entry).getValue(), fakeChampValue));
			}
		}
		pushComportements(marshaller, tmp);
	}
	
	@Override
	protected Class<?> getTypeObjProblemeHibernate(Object object) {
		Class<?> clazz = object.getClass();
		if(clazz.getName().toLowerCase().indexOf("hibernate") != -1){
			if(object.getClass().getName().toLowerCase().indexOf("persistentmap") != -1)
				return HashMap.class;
			if(object.getClass().getName().toLowerCase().indexOf("persistentsortedmap") != -1)
				return TreeMap.class;
		}
		return clazz;
	}
}
