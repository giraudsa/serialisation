package giraudsa.marshall.serialisation.text.xml.actions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.atomic.AtomicIntegerArray;

import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.text.xml.ActionXml;
import utils.champ.FakeChamp;
import utils.champ.FieldInformations;

public class ActionXmlAtomicArrayIntegerType  extends ActionXml<AtomicIntegerArray> {
	
	public ActionXmlAtomicArrayIntegerType() {
		super();
	}
	
	@Override
	protected void ecritValeur(Marshaller marshaller, AtomicIntegerArray array, FieldInformations fieldInformations) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException, IOException{
		FakeChamp fakeChamp = new FakeChamp("V", Integer.class, fieldInformations.getRelation());
		Deque<Comportement> tmp = new ArrayDeque<>();
		for (int i = 0; i < array.length(); ++i) {
			tmp.push(traiteChamp(marshaller, array.get(i), fakeChamp));
		}
		pushComportements(marshaller, tmp);
	}
}
