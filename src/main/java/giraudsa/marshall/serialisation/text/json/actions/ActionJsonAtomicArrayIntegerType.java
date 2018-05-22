package giraudsa.marshall.serialisation.text.json.actions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.atomic.AtomicIntegerArray;

import giraudsa.marshall.exception.MarshallExeption;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.text.json.ActionJson;
import utils.Constants;
import utils.champ.FakeChamp;
import utils.champ.FieldInformations;

public class ActionJsonAtomicArrayIntegerType extends ActionJson<AtomicIntegerArray> {

	public ActionJsonAtomicArrayIntegerType() {
		super();
	}

	@Override
	protected void clotureObject(final Marshaller marshaller, final AtomicIntegerArray obj, final boolean typeDevinable)
			throws IOException {
		if (typeDevinable)
			fermeCrochet(marshaller, obj.length() != 0);
		else {
			fermeCrochet(marshaller, obj.length() != 0);
			fermeAccolade(marshaller);
		}
	}

	@Override
	protected boolean commenceObject(final Marshaller marshaller, final AtomicIntegerArray obj,
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
	protected void ecritValeur(final Marshaller marshaller, final AtomicIntegerArray array, final FieldInformations fi,
			boolean ecrisSeparateur) throws IllegalAccessException, InstantiationException, InvocationTargetException,
			NoSuchMethodException, NotImplementedSerializeException, IOException, MarshallExeption {
		final FakeChamp fakeChamp = new FakeChamp("V", Integer.class, fi.getRelation(), fi.getAnnotations());
		final Deque<Comportement> tmp = new ArrayDeque<>();
		for (int i = 0; i < array.length(); ++i) {
			tmp.push(traiteChamp(marshaller, array.get(i), fakeChamp, ecrisSeparateur));
			ecrisSeparateur = true;
		}
		pushComportements(marshaller, tmp);
	}
}
