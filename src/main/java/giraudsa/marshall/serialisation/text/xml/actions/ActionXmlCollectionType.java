package giraudsa.marshall.serialisation.text.xml.actions;

import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.text.xml.ActionXml;
import utils.Constants;
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
		String clef = genericType instanceof Class ? ((Class<?>)genericType).getSimpleName() : "Value";
		FakeChamp fakeChamp = new FakeChamp(clef, genericType, fieldInformations.getRelation());
		Deque<Comportement> tmp = new ArrayDeque<>();
		Collection<?> collection = (Collection<?>) obj;
		for (Object value : collection) {
			tmp.push(traiteChamp(marshaller,value, fakeChamp));
		}
		pushComportements(marshaller, tmp);
	}

	@Override
	public void writeType(Marshaller marshaller, Class<?> type) throws IOException{
		write(marshaller," type=\"");
		String type1 = Constants.getSmallNameType(type);
		write(marshaller, type1);
		write(marshaller, "\"");
	}
}
