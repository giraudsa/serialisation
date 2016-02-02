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

@SuppressWarnings("rawtypes")
public class ActionXmlCollectionType extends ActionXml<Collection> {
	
	public ActionXmlCollectionType() {
		super();
	}

	@Override
	protected Class<?> getType(Collection obj) {
		return (obj.getClass().getName().toLowerCase().indexOf("hibernate") != -1) ? ArrayList.class : obj.getClass();
	}

	@Override
	protected void ecritValeur(Marshaller marshaller,Collection obj, FieldInformations fieldInformations) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException, IOException{
		Type[] types = fieldInformations.getParametreType();
		Type genericType = Object.class;
		if(types != null && types.length > 0){
			genericType = types[0];
		}
		FakeChamp fakeChamp = new FakeChamp("V", genericType, fieldInformations.getRelation());
		Deque<Comportement> tmp = new ArrayDeque<Comportement>();
		Collection<?> collection = (Collection<?>) obj;
		for (Object value : collection) {
			tmp.push(traiteChamp(marshaller,value, fakeChamp));
		}
		pushComportements(marshaller, tmp);
	}

}
