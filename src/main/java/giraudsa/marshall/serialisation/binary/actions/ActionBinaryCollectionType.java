package giraudsa.marshall.serialisation.binary.actions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.TreeSet;

import giraudsa.marshall.exception.MarshallExeption;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.binary.ActionBinary;
import utils.champ.FakeChamp;
import utils.champ.FieldInformations;

@SuppressWarnings("rawtypes")
public class ActionBinaryCollectionType extends ActionBinary<Collection> {

	public ActionBinaryCollectionType() {
		super();
	}

	@Override
	protected void ecritValeur(final Marshaller marshaller, final Collection obj, final FieldInformations fi,
			final boolean isDejaVu) throws IOException, IllegalAccessException, InstantiationException,
			InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException, MarshallExeption {
		final var types = fi.getParametreType();
		Type genericType = (types != null && types.length > 0) ? types[0] : Object.class;
		final var fakeChamp = new FakeChamp(null, genericType, fi.getRelation(), fi.getAnnotations());

		final var tmp = new ArrayDeque<Comportement>();
		if (!isDejaVu) {
			if (strategieSerialiseTout(marshaller, fi)) {
				setDejaTotalementSerialise(marshaller, obj);
			}
			writeInt(marshaller, obj.size());
			for (final var value : obj) {
				tmp.push(traiteChamp(marshaller, value, fakeChamp));
			}
		} else if (!isDejaTotalementSerialise(marshaller, obj) && strategieSerialiseTout(marshaller, fi)) {
			setDejaTotalementSerialise(marshaller, obj);
			for (final var value : obj) {
				tmp.push(traiteChamp(marshaller, value, fakeChamp));
			}
		}
		pushComportements(marshaller, tmp);
	}

	@Override
	protected Class<?> getTypeObjProblemeHibernate(final Object object) {
		final var clazz = object.getClass();
		final var className = clazz.getName().toLowerCase();

		if (!className.contains("hibernate")) {
			return clazz;
		}

		if (className.contains("persistentbag")) {
			return ArrayList.class;
		}
		if (className.contains("persistentset")) {
			return HashSet.class;
		}
		if (className.contains("persistentsortedset")) {
			return TreeSet.class;
		}
		return ArrayList.class;
	}
}
