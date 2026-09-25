package io.github.giraudsa.fidelis.serialisation.text.json.actions;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.Deque;

import io.github.giraudsa.fidelis.exception.MarshallExeption;
import io.github.giraudsa.fidelis.exception.NotImplementedSerializeException;
import io.github.giraudsa.fidelis.serialisation.Marshaller;
import io.github.giraudsa.fidelis.serialisation.text.json.ActionJson;
import io.github.giraudsa.fidelis.utils.Constants;
import io.github.giraudsa.fidelis.utils.champ.FakeChamp;
import io.github.giraudsa.fidelis.utils.champ.FieldInformations;

public class ActionJsonArrayType extends ActionJson<Object> {

	public ActionJsonArrayType() {
		super();
	}

	@Override
	protected void clotureObject(final Marshaller marshaller, final Object obj, final boolean typeDevinable)
			throws IOException {
		if (typeDevinable)
			fermeCrochet(marshaller, Array.getLength(obj) != 0);
		else {
			fermeCrochet(marshaller, Array.getLength(obj) != 0);
			fermeAccolade(marshaller);
		}
	}

	@Override
	protected boolean commenceObject(final Marshaller marshaller, final Object obj, final boolean typeDevinable)
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
	protected void ecritValeur(final Marshaller marshaller, final Object obj, final FieldInformations fi,
			boolean ecrisSeparateur) throws IOException, IllegalAccessException, InstantiationException,
			InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException, MarshallExeption {
		final Type genericType = obj.getClass().getComponentType();
		final FakeChamp fakeChamp = new FakeChamp(null, genericType, fi.getRelation(), fi.getAnnotations());
		if (ecritureDirecte(marshaller)) {
			for (int i = 0; i < Array.getLength(obj); ++i) {
				ecritDirect(marshaller, Array.get(obj, i), fakeChamp, ecrisSeparateur);
				ecrisSeparateur = true;
			}
			return;
		}
		final Deque<Comportement> tmp = new ArrayDeque<>();
		for (int i = 0; i < Array.getLength(obj); ++i) {
			tmp.push(traiteChamp(marshaller, Array.get(obj, i), fakeChamp, ecrisSeparateur));
			ecrisSeparateur = true;
		}
		pushComportements(marshaller, tmp);
	}
}
