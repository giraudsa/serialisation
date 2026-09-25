package io.github.giraudsa.fidelis.serialisation.binary.actions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.function.BiConsumer;

import io.github.giraudsa.fidelis.exception.MarshallExeption;
import io.github.giraudsa.fidelis.exception.NotImplementedSerializeException;
import io.github.giraudsa.fidelis.serialisation.Marshaller;
import io.github.giraudsa.fidelis.serialisation.binary.ActionBinary;
import io.github.giraudsa.fidelis.utils.champ.FakeChamp;
import io.github.giraudsa.fidelis.utils.champ.FieldInformations;
import io.github.giraudsa.fidelis.utils.TypeExtension;

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

	/** Écrit les entrées par l'itération interne de la map (sans itérateur ni Entry), même ordre que entrySet. */
	private static final class EcritureEntrees implements BiConsumer<Object, Object> {
		private Exception erreur;
		private final FakeChamp fakeChampKey;
		private final FakeChamp fakeChampValue;
		private final Marshaller marshaller;

		private EcritureEntrees(final Marshaller marshaller, final FakeChamp fakeChampKey,
				final FakeChamp fakeChampValue) {
			this.marshaller = marshaller;
			this.fakeChampKey = fakeChampKey;
			this.fakeChampValue = fakeChampValue;
		}

		@Override
		public void accept(final Object cle, final Object valeur) {
			if (erreur != null)
				return;
			try {
				ecritOuDiffere(binaryMarshaller(marshaller), cle, fakeChampKey);
				ecritOuDiffere(binaryMarshaller(marshaller), valeur, fakeChampValue);
			} catch (NotImplementedSerializeException | MarshallExeption e) {
				erreur = e;
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void ecritEntrees(final Marshaller marshaller, final Map map, final FakeChamp fakeChampKey,
			final FakeChamp fakeChampValue) throws NotImplementedSerializeException, MarshallExeption {
		final Class<?> classe = map.getClass();
		if (classe == LinkedHashMap.class || classe == HashMap.class) {
			final EcritureEntrees ecriture = new EcritureEntrees(marshaller, fakeChampKey, fakeChampValue);
			map.forEach(ecriture);
			if (ecriture.erreur instanceof NotImplementedSerializeException)
				throw (NotImplementedSerializeException) ecriture.erreur;
			if (ecriture.erreur != null)
				throw (MarshallExeption) ecriture.erreur;
			return;
		}
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
