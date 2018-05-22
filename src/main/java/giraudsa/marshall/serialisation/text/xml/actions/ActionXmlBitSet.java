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

public class ActionXmlBitSet extends ActionXml<BitSet> {

	public ActionXmlBitSet() {
		super();
	}

	@Override
	protected void ecritValeur(final Marshaller marshaller, final BitSet array, final FieldInformations fi,
			final boolean serialiseTout)
			throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException,
			NotImplementedSerializeException, IOException, MarshallExeption {
		final FakeChamp fakeChamp = new FakeChamp("bit", boolean.class, TypeRelation.COMPOSITION, fi.getAnnotations());
		final Deque<Comportement> tmp = new ArrayDeque<>();
		for (int i = 0; i < array.length(); ++i)
			tmp.push(traiteChamp(marshaller, array.get(i), fakeChamp));
		pushComportements(marshaller, tmp);
	}

	@Override
	protected void pushComportementParticulier(final Marshaller marshaller, final BitSet obj, final String nomBalise,
			final FieldInformations fieldInformations) {
		if (!obj.isEmpty()) {
			pushComportement(marshaller, newComportementFermeBalise(nomBalise));
			pushComportement(marshaller, newComportementOuvreBaliseEtEcritValeur(obj, nomBalise, fieldInformations));
		} else
			pushComportement(marshaller, newComportementOuvreEtFermeBalise(obj, nomBalise, fieldInformations));
	}
}
