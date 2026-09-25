package io.github.giraudsa.fidelis.serialisation.text.json.actions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayDeque;
import java.util.BitSet;
import java.util.Deque;

import io.github.giraudsa.fidelis.annotations.TypeRelation;
import io.github.giraudsa.fidelis.exception.MarshallExeption;
import io.github.giraudsa.fidelis.exception.NotImplementedSerializeException;
import io.github.giraudsa.fidelis.serialisation.Marshaller;
import io.github.giraudsa.fidelis.serialisation.text.json.ActionJson;
import io.github.giraudsa.fidelis.utils.Constants;
import io.github.giraudsa.fidelis.utils.champ.FakeChamp;
import io.github.giraudsa.fidelis.utils.champ.FieldInformations;

public class ActionJsonBitSet extends ActionJson<BitSet> {

	public ActionJsonBitSet() {
		super();
	}

	@Override
	protected void clotureObject(final Marshaller marshaller, final BitSet obj, final boolean typeDevinable)
			throws IOException {
		if (typeDevinable)
			fermeCrochet(marshaller, !obj.isEmpty());
		else {
			fermeCrochet(marshaller, !obj.isEmpty());
			fermeAccolade(marshaller);
		}
	}

	@Override
	protected boolean commenceObject(final Marshaller marshaller, final BitSet obj, final boolean typeDevinable)
			throws IOException {
		if (typeDevinable)
			ouvreCrochet(marshaller);
		else {// type inconnu pour deserialisation
			ouvreAccolade(marshaller);
			ecritType(marshaller, obj);
			writeSeparator(marshaller);
			ecritClef(marshaller, Constants.VALEUR);
			ouvreCrochet(marshaller);
		}
		return false;
	}

	@Override
	protected void ecritValeur(final Marshaller marshaller, final BitSet array, final FieldInformations fi,
			boolean ecrisSeparateur) throws IllegalAccessException, InstantiationException, InvocationTargetException,
			NoSuchMethodException, NotImplementedSerializeException, IOException, MarshallExeption {
		final FakeChamp fakeChamp = new FakeChamp(null, boolean.class, TypeRelation.COMPOSITION, fi.getAnnotations());
		final Deque<Comportement> tmp = new ArrayDeque<>();
		for (int i = 0; i < array.length(); ++i) {
			tmp.push(traiteChamp(marshaller, array.get(i), fakeChamp, ecrisSeparateur));
			ecrisSeparateur = true;
		}
		pushComportements(marshaller, tmp);
	}
}
