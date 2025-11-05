package giraudsa.marshall.serialisation.binary.actions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import giraudsa.marshall.exception.MarshallExeption;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.binary.ActionBinary;
import utils.champ.FakeChamp;
import utils.champ.FieldInformations;

@SuppressWarnings("rawtypes")
public class ActionBinaryDictionaryType extends ActionBinary<Map> {

	public ActionBinaryDictionaryType() {
		super();
	}

	@Override
	protected void ecritValeur(final Marshaller marshaller, final Map map, final FieldInformations fi,
			final boolean isDejaVu) throws IOException, IllegalAccessException, InstantiationException,
			InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException, MarshallExeption {
		final var types = fi.getParametreType();
		Type genericTypeKey = (types != null && types.length > 1) ? types[0] : Object.class;
		Type genericTypeValue = (types != null && types.length > 1) ? types[1] : Object.class;

		final var fakeChampKey = new FakeChamp("K", genericTypeKey, fi.getRelation(), fi.getAnnotations());
		final var fakeChampValue = new FakeChamp("V", genericTypeValue, fi.getRelation(), fi.getAnnotations());

		final var tmp = new ArrayDeque<Comportement>();
		if (!isDejaVu) {
			if (strategieSerialiseTout(marshaller, fi)) {
				setDejaTotalementSerialise(marshaller, map);
			}
			writeInt(marshaller, map.size());
			for (final var entry : map.entrySet()) {
				tmp.push(traiteChamp(marshaller, entry.getKey(), fakeChampKey));
				tmp.push(traiteChamp(marshaller, entry.getValue(), fakeChampValue));
			}
		} else if (!isDejaTotalementSerialise(marshaller, map) && strategieSerialiseTout(marshaller, fi)) {
			setDejaTotalementSerialise(marshaller, map);
			for (final var entry : map.entrySet()) {
				tmp.push(traiteChamp(marshaller, entry.getKey(), fakeChampKey));
				tmp.push(traiteChamp(marshaller, entry.getValue(), fakeChampValue));
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

		if (className.contains("persistentmap")) {
			return HashMap.class;
		}
		if (className.contains("persistentsortedmap")) {
			return TreeMap.class;
		}
		return clazz;
	}
}
