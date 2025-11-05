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
		final var fakeChamp = new FakeChamp(null, obj.getClass().getComponentType(), fi.getRelation(), fi.getAnnotations());
		final var tmp = new ArrayDeque<Comportement>();
		if (!isDejaVu) {
			if (strategieSerialiseTout(marshaller, fi)) {
				setDejaTotalementSerialise(marshaller, obj);
			}
			final var size = Array.getLength(obj);
			writeInt(marshaller, size);
			for (var i = 0; i < size; i++) {
				tmp.push(traiteChamp(marshaller, Array.get(obj, i), fakeChamp));
			}
		} else if (!isDejaTotalementSerialise(marshaller, obj) && strategieSerialiseTout(marshaller, fi)) {
			// deja vu, donc on passe ici qd la relation est de type COMPOSITION
			setDejaTotalementSerialise(marshaller, obj);
			final var length = Array.getLength(obj);
			for (var i = 0; i < length; i++) {
				tmp.push(traiteChamp(marshaller, Array.get(obj, i), fakeChamp));
			}
		}
		pushComportements(marshaller, tmp);
	}
}
