package giraudsa.marshall.serialisation.binary.actions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.TreeSet;

import giraudsa.marshall.exception.MarshallExeption;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.binary.ActionBinary;
import utils.champ.FakeChamp;
import utils.champ.FieldInformations;
import utils.TypeExtension;

@SuppressWarnings("rawtypes")
public class ActionBinaryCollectionType extends ActionBinary<Collection> {

	public ActionBinaryCollectionType() {
		super();
	}

	@Override
	protected void ecritValeur(final Marshaller marshaller, final Collection obj, final FieldInformations fi,
			final boolean isDejaVu) throws IOException, IllegalAccessException, InstantiationException,
			InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException, MarshallExeption {
		final FakeChamp fakeChamp = fi.getChampParametre(FieldInformations.ELEMENT);
		if (!isDejaVu) {
			if (strategieSerialiseTout(marshaller, fi))
				setDejaTotalementSerialise(marshaller, obj);
			writeVarInt(marshaller, obj.size());
			for (final Object value : obj)
				ecritOuDiffere(marshaller, value, fakeChamp);
		} else if (!isDejaTotalementSerialise(marshaller, obj) && strategieSerialiseTout(marshaller, fi)) {
			setDejaTotalementSerialise(marshaller, obj);
			for (final Object value : obj)
				ecritOuDiffere(marshaller, value, fakeChamp);
		}
		empileDifferes(marshaller);
	}

	@Override
	protected boolean isFeuille() {
		return false;
	}

	@Override
	protected Class<?> getTypeObjProblemeHibernate(final Object object) {
		final Class<?> clazz = object.getClass();

		if (TypeExtension.isHibernate(clazz)) {
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
