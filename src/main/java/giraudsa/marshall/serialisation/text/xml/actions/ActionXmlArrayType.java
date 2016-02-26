package giraudsa.marshall.serialisation.text.xml.actions;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.Stack;

import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.text.xml.ActionXml;
import utils.champ.FakeChamp;
import utils.champ.FieldInformations;

public class ActionXmlArrayType  extends ActionXml<Object> {
	
	public ActionXmlArrayType() {
		super();
	}
	
	@Override
	protected void ecritValeur(Marshaller marshaller, Object obj, FieldInformations fieldInformations) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException, IOException{
		Type genericType = obj.getClass().getComponentType();
		FakeChamp fakeChamp = new FakeChamp("V", genericType, fieldInformations.getRelation());
		Stack<Comportement> tmp = new Stack<Comportement>();
		for (int i = 0; i < Array.getLength(obj); ++i) {
			tmp.push(traiteChamp(marshaller, Array.get(obj, i), fakeChamp));
		}
		pushComportements(marshaller, tmp);
	}
}
