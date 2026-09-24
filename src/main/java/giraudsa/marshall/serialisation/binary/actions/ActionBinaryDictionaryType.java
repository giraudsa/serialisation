package giraudsa.marshall.serialisation.binary.actions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
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
import utils.TypeExtension;

@SuppressWarnings("rawtypes")
public class ActionBinaryDictionaryType extends ActionBinary<Map> {

	public ActionBinaryDictionaryType() {
		super();
	}

	@Override
	protected void ecritValeur(final Marshaller marshaller, final Map map, final FieldInformations fi,
			final boolean isDejaVu) throws IOException, IllegalAccessException, InstantiationException,
			InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException, MarshallExeption {
		final FakeChamp fakeChampKey = fi.getChampParametre(FieldInformations.CLE);
		final FakeChamp fakeChampValue = fi.getChampParametre(FieldInformations.VALEUR);
		if (!isDejaVu) {
			if (strategieSerialiseTout(marshaller, fi))
				setDejaTotalementSerialise(marshaller, map);
			writeVarInt(marshaller, map.size());
			ecritEntrees(marshaller, map, fakeChampKey, fakeChampValue);
		} else if (!isDejaTotalementSerialise(marshaller, map) && strategieSerialiseTout(marshaller, fi)) {
			setDejaTotalementSerialise(marshaller, map);
			ecritEntrees(marshaller, map, fakeChampKey, fakeChampValue);
		}
		empileDifferes(marshaller);
	}

	private void ecritEntrees(final Marshaller marshaller, final Map map, final FakeChamp fakeChampKey,
			final FakeChamp fakeChampValue) throws NotImplementedSerializeException, MarshallExeption {
		for (final Object entry : map.entrySet()) {
			ecritOuDiffere(marshaller, ((Entry) entry).getKey(), fakeChampKey);
			ecritOuDiffere(marshaller, ((Entry) entry).getValue(), fakeChampValue);
		}
	}

	@Override
	protected boolean isFeuille() {
		return false;
	}

	@Override
	protected Class<?> getTypeObjProblemeHibernate(final Object object) {
		final Class<?> clazz = object.getClass();
		if (TypeExtension.isHibernate(clazz)) {
			if (object.getClass().getName().toLowerCase().indexOf("persistentmap") != -1)
				return HashMap.class;
			if (object.getClass().getName().toLowerCase().indexOf("persistentsortedmap") != -1)
				return TreeMap.class;
		}
		return clazz;
	}
}
