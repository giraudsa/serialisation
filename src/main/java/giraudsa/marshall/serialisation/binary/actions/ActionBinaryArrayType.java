package giraudsa.marshall.serialisation.binary.actions;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayDeque;
import java.util.Deque;

import giraudsa.marshall.exception.MarshallExeption;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.binary.ActionBinary;
import utils.champ.FakeChamp;
import utils.champ.FieldInformations;

public class ActionBinaryArrayType extends ActionBinary<Object> {

	public ActionBinaryArrayType() {
		super();
	}

	@Override
	protected void ecritValeur(final Marshaller marshaller, final Object obj, final FieldInformations fi,
			final boolean isDejaVu) throws IOException, IllegalAccessException, InstantiationException,
			InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException, MarshallExeption {
		final FakeChamp fakeChamp = new FakeChamp(null, obj.getClass().getComponentType(), fi.getRelation(),
				fi.getAnnotations());
		final Deque<Comportement> tmp = new ArrayDeque<>();
		if (!isDejaVu) {
			if (strategieSerialiseTout(marshaller, fi))
				setDejaTotalementSerialise(marshaller, obj);
			final int size = Array.getLength(obj);
			writeInt(marshaller, size);
			for (int i = 0; i < size; i++)
				tmp.push(traiteChamp(marshaller, Array.get(obj, i), fakeChamp));
		} else if (!isDejaTotalementSerialise(marshaller, obj) && strategieSerialiseTout(marshaller, fi)) {// deja vu,
																											// donc on
																											// passe ici
																											// qd la
																											// relation
																											// est de
																											// type
																											// COMPOSITION
			setDejaTotalementSerialise(marshaller, obj);
			for (int i = 0; i < Array.getLength(obj); i++)
				tmp.push(traiteChamp(marshaller, Array.get(obj, i), fakeChamp));
		}
		pushComportements(marshaller, tmp);
	}
}
