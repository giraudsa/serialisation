package giraudsa.marshall.serialisation.text.xml.actions;

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
import giraudsa.marshall.serialisation.text.xml.ActionXml;
import utils.champ.FakeChamp;
import utils.champ.FieldInformations;

@SuppressWarnings("rawtypes")
public class ActionXmlCollectionType extends ActionXml<Collection> {

	public ActionXmlCollectionType() {
		super();
	}

	@Override
	protected void ecritValeur(final Marshaller marshaller, final Collection obj,
			final FieldInformations fieldInformations, final boolean serialiseTout)
			throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException,
			NotImplementedSerializeException, IOException, MarshallExeption {
		final Type[] types = fieldInformations.getParametreType();
		Type genericType = Object.class;
		if (types != null && types.length > 0)
			genericType = types[0];
		final String clef = genericType instanceof Class ? ((Class<?>) genericType).getSimpleName() : "Value";
		final FakeChamp fakeChamp = new FakeChamp(clef, genericType, fieldInformations.getRelation(),
				fieldInformations.getAnnotations());
		final Deque<Comportement> tmp = new ArrayDeque<>();
		final Collection<?> collection = obj;
		for (final Object value : collection)
			tmp.push(traiteChamp(marshaller, value, fakeChamp));
		pushComportements(marshaller, tmp);
	}

	@Override
	protected Class<?> getType(final Collection obj) {
		final Class<?> clazz = obj.getClass();

		if (clazz.getName().toLowerCase().indexOf("hibernate") != -1) {
			if (obj.getClass().getName().toLowerCase().indexOf("persistentlist") != -1)
				return ArrayList.class;
			if (obj.getClass().getName().toLowerCase().indexOf("persistentbag") != -1)
				return ArrayList.class;
			if (obj.getClass().getName().toLowerCase().indexOf("persistentset") != -1)
				return HashSet.class;
			if (obj.getClass().getName().toLowerCase().indexOf("persistentsortedset") != -1)
				return TreeSet.class;
			else
				return ArrayList.class;
		}
		return clazz;
	}

	@Override
	protected void pushComportementParticulier(final Marshaller marshaller, final Collection obj,
			final String nomBalise, final FieldInformations fieldInformations) {
		if (!obj.isEmpty()) {
			pushComportement(marshaller, newComportementFermeBalise(nomBalise));
			pushComportement(marshaller, newComportementOuvreBaliseEtEcritValeur(obj, nomBalise, fieldInformations));
		} else
			pushComportement(marshaller, newComportementOuvreEtFermeBalise(obj, nomBalise, fieldInformations));
	}

}
