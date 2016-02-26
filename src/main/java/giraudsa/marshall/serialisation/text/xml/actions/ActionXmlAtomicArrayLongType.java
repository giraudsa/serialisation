package giraudsa.marshall.serialisation.text.xml.actions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicLongArray;

import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.text.xml.ActionXml;
import utils.champ.FakeChamp;
import utils.champ.FieldInformations;

public class ActionXmlAtomicArrayLongType  extends ActionXml<AtomicLongArray> {
	
	public ActionXmlAtomicArrayLongType() {
		super();
	}
	
	@Override
	protected void ecritValeur(Marshaller marshaller, AtomicLongArray obj, FieldInformations fieldInformations) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException, IOException{
		FakeChamp fakeChamp = new FakeChamp("V", Long.class, fieldInformations.getRelation());
		Stack<Comportement> tmp = new Stack<Comportement>();
		for (int i = 0; i < obj.length(); ++i) {
			tmp.push(traiteChamp(marshaller, obj.get(i), fakeChamp));
		}
		pushComportements(marshaller, tmp);
	}
}
