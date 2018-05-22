package giraudsa.marshall.serialisation.text.xml.actions;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.Deque;

import giraudsa.marshall.exception.MarshallExeption;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.text.xml.ActionXml;
import utils.champ.FakeChamp;
import utils.champ.FieldInformations;

public class ActionXmlArrayType extends ActionXml<Object> {

	public ActionXmlArrayType() {
		super();
	}

	@Override
	protected void ecritValeur(final Marshaller marshaller, final Object obj, final FieldInformations fi,
			final boolean serialiseTout)
			throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException,
			NotImplementedSerializeException, IOException, MarshallExeption {
		final Type genericType = obj.getClass().getComponentType();
		final String clef = genericType instanceof Class ? ((Class<?>) genericType).getSimpleName() : "Value";
		final FakeChamp fakeChamp = new FakeChamp(clef, genericType, fi.getRelation(), fi.getAnnotations());
		final Deque<Comportement> tmp = new ArrayDeque<>();
		for (int i = 0; i < Array.getLength(obj); ++i)
			tmp.push(traiteChamp(marshaller, Array.get(obj, i), fakeChamp));
		pushComportements(marshaller, tmp);
	}

	@Override
	protected void pushComportementParticulier(final Marshaller marshaller, final Object obj, final String nomBalise,
			final FieldInformations fieldInformations) {
		if (Array.getLength(obj) > 0) {
			pushComportement(marshaller, newComportementFermeBalise(nomBalise));
			pushComportement(marshaller, newComportementOuvreBaliseEtEcritValeur(obj, nomBalise, fieldInformations));
		} else
			pushComportement(marshaller, newComportementOuvreEtFermeBalise(obj, nomBalise, fieldInformations));
	}
}
