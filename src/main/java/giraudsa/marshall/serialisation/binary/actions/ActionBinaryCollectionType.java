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
		final Type[] types = fi.getParametreType();
		Type genericType = Object.class;
		if (types != null && types.length > 0)
			genericType = types[0];
		final FakeChamp fakeChamp = new FakeChamp(null, genericType, fi.getRelation(), fi.getAnnotations());

		final Deque<Comportement> tmp = new ArrayDeque<>();
		if (!isDejaVu) {
			if (strategieSerialiseTout(marshaller, fi))
				setDejaTotalementSerialise(marshaller, obj);
			writeInt(marshaller, obj.size());
			for (final Object value : obj)
				tmp.push(traiteChamp(marshaller, value, fakeChamp));
		} else if (!isDejaTotalementSerialise(marshaller, obj) && strategieSerialiseTout(marshaller, fi)) {
			setDejaTotalementSerialise(marshaller, obj);
			for (final Object value : obj)
				tmp.push(traiteChamp(marshaller, value, fakeChamp));
		}
		pushComportements(marshaller, tmp);
	}

	@Override
	protected Class<?> getTypeObjProblemeHibernate(final Object object) {
		final Class<?> clazz = object.getClass();

		if (clazz.getName().toLowerCase().indexOf("hibernate") != -1) {
			if (object.getClass().getName().toLowerCase().indexOf("persistentbag") != -1)
				return ArrayList.class;
			if (object.getClass().getName().toLowerCase().indexOf("persistentset") != -1)
				return HashSet.class;
			if (object.getClass().getName().toLowerCase().indexOf("persistentsortedset") != -1)
				return TreeSet.class;
			else
				return ArrayList.class;
		}
		return clazz;
	}
}
