package giraudsa.marshall.serialisation.text.xml.actions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayDeque;
import java.util.BitSet;
import java.util.Deque;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.MarshallExeption;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.text.xml.ActionXml;
import utils.champ.FakeChamp;
import utils.champ.FieldInformations;

public class ActionXmlBitSet  extends ActionXml<BitSet> {
	
	public ActionXmlBitSet() {
		super();
	}
	
	@Override
	protected void ecritValeur(Marshaller marshaller, BitSet array, FieldInformations fieldInformations) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException, IOException, MarshallExeption{
		FakeChamp fakeChamp = new FakeChamp("bit", boolean.class, TypeRelation.COMPOSITION);
		Deque<Comportement> tmp = new ArrayDeque<>();
		for (int i = 0; i < array.length(); ++i) {
			tmp.push(traiteChamp(marshaller, array.get(i), fakeChamp));
		}
		pushComportements(marshaller, tmp);
	}
}
