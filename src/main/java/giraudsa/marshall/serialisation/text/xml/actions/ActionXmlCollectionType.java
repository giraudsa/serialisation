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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.TreeSet;

@SuppressWarnings("rawtypes")
public class ActionXmlCollectionType extends ActionXml<Collection> {
	
	public ActionXmlCollectionType() {
		super();
	}

	@Override
	protected Class<?> getType(Collection obj) {
		Class<?> clazz = obj.getClass();
		
		if(clazz.getName().toLowerCase().indexOf("hibernate") != -1){
			if(obj.getClass().getName().toLowerCase().indexOf("persistentbag") != -1)
				return ArrayList.class;
			if(obj.getClass().getName().toLowerCase().indexOf("persistentset") != -1)
				return HashSet.class;
			if(obj.getClass().getName().toLowerCase().indexOf("persistentsortedset") != -1)
				return TreeSet.class;
		}
		return clazz;
	}

	@Override
	protected void ecritValeur(Marshaller marshaller,Collection obj, FieldInformations fieldInformations) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException, IOException{
		Type[] types = fieldInformations.getParametreType();
		Type genericType = Object.class;
		if(types != null && types.length > 0){
			genericType = types[0];
		}
		String clef = genericType instanceof Class ? ((Class<?>)genericType).getSimpleName() : "Value";
		FakeChamp fakeChamp = new FakeChamp(clef, genericType, fieldInformations.getRelation());
		Deque<Comportement> tmp = new ArrayDeque<>();
		Collection<?> collection = (Collection<?>) obj;
		for (Object value : collection) {
			tmp.push(traiteChamp(marshaller,value, fakeChamp));
		}
		pushComportements(marshaller, tmp);
	}

}
