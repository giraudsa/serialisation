package io.github.giraudsa.fidelis.serialisation.binary.actions;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;

import io.github.giraudsa.fidelis.exception.MarshallExeption;
import io.github.giraudsa.fidelis.exception.NotImplementedSerializeException;
import io.github.giraudsa.fidelis.serialisation.Marshaller;
import io.github.giraudsa.fidelis.serialisation.binary.ActionBinary;
import io.github.giraudsa.fidelis.utils.champ.FakeChamp;
import io.github.giraudsa.fidelis.utils.champ.FieldInformations;

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
		if (!isDejaVu) {
			if (strategieSerialiseTout(marshaller, fi))
				setDejaTotalementSerialise(marshaller, obj);
			final int size = Array.getLength(obj);
			writeVarInt(marshaller, size);
			for (int i = 0; i < size; i++)
				ecritOuDiffere(marshaller, Array.get(obj, i), fakeChamp);
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
				ecritOuDiffere(marshaller, Array.get(obj, i), fakeChamp);
		}
		empileDifferes(marshaller);
	}

	@Override
	protected boolean isFeuille() {
		return false;
	}
}
