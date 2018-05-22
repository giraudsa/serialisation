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
		final Type[] types = fi.getParametreType();
		Type genericTypeKey = Object.class;
		Type genericTypeValue = Object.class;
		if (types != null && types.length > 1) {
			genericTypeKey = types[0];
			genericTypeValue = types[1];
		}
		final FakeChamp fakeChampKey = new FakeChamp("K", genericTypeKey, fi.getRelation(), fi.getAnnotations());
		final FakeChamp fakeChampValue = new FakeChamp("V", genericTypeValue, fi.getRelation(), fi.getAnnotations());

		final Deque<Comportement> tmp = new ArrayDeque<>();
		if (!isDejaVu) {
			if (strategieSerialiseTout(marshaller, fi))
				setDejaTotalementSerialise(marshaller, map);
			writeInt(marshaller, map.size());
			for (final Object entry : map.entrySet()) {
				tmp.push(traiteChamp(marshaller, ((Entry) entry).getKey(), fakeChampKey));
				tmp.push(traiteChamp(marshaller, ((Entry) entry).getValue(), fakeChampValue));
			}
		} else if (!isDejaTotalementSerialise(marshaller, map) && strategieSerialiseTout(marshaller, fi)) {
			setDejaTotalementSerialise(marshaller, map);
			for (final Object entry : map.entrySet()) {
				tmp.push(traiteChamp(marshaller, ((Entry) entry).getKey(), fakeChampKey));
				tmp.push(traiteChamp(marshaller, ((Entry) entry).getValue(), fakeChampValue));
			}
		}
		pushComportements(marshaller, tmp);
	}

	@Override
	protected Class<?> getTypeObjProblemeHibernate(final Object object) {
		final Class<?> clazz = object.getClass();
		if (clazz.getName().toLowerCase().indexOf("hibernate") != -1) {
			if (object.getClass().getName().toLowerCase().indexOf("persistentmap") != -1)
				return HashMap.class;
			if (object.getClass().getName().toLowerCase().indexOf("persistentsortedmap") != -1)
				return TreeMap.class;
		}
		return clazz;
	}
}
