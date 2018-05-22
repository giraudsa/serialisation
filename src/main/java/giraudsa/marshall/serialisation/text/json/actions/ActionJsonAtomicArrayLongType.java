package giraudsa.marshall.serialisation.text.json.actions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.atomic.AtomicLongArray;

import giraudsa.marshall.exception.MarshallExeption;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.text.json.ActionJson;
import utils.Constants;
import utils.champ.FakeChamp;
import utils.champ.FieldInformations;

public class ActionJsonAtomicArrayLongType extends ActionJson<AtomicLongArray> {

	public ActionJsonAtomicArrayLongType() {
		super();
	}

	@Override
	protected void clotureObject(final Marshaller marshaller, final AtomicLongArray obj, final boolean typeDevinable)
			throws IOException {
		if (typeDevinable)
			fermeCrochet(marshaller, obj.length() != 0);
		else {
			fermeCrochet(marshaller, obj.length() != 0);
			fermeAccolade(marshaller);
		}
	}

	@Override
	protected boolean commenceObject(final Marshaller marshaller, final AtomicLongArray obj,
			final boolean typeDevinable) throws IOException {
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
	protected void ecritValeur(final Marshaller marshaller, final AtomicLongArray obj, final FieldInformations fi,
			boolean ecrisSeparateur) throws IllegalAccessException, InstantiationException, InvocationTargetException,
			NoSuchMethodException, NotImplementedSerializeException, IOException, MarshallExeption {
		final FakeChamp fakeChamp = new FakeChamp("V", Long.class, fi.getRelation(), fi.getAnnotations());
		final Deque<Comportement> tmp = new ArrayDeque<>();
		for (int i = 0; i < obj.length(); ++i) {
			tmp.push(traiteChamp(marshaller, obj.get(i), fakeChamp, ecrisSeparateur));
			ecrisSeparateur = true;
		}
		pushComportements(marshaller, tmp);
	}
}
