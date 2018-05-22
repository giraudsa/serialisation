package giraudsa.marshall.serialisation.text.xml.actions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.Map.Entry;

import giraudsa.marshall.exception.MarshallExeption;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.text.xml.ActionXml;
import utils.champ.FakeChamp;
import utils.champ.FieldInformations;

@SuppressWarnings("rawtypes")
public class ActionXmlDictionaryType extends ActionXml<Map> {

	public ActionXmlDictionaryType() {
		super();
	}

	@Override
	protected void ecritValeur(final Marshaller marshaller, final Map obj, final FieldInformations fieldInformations,
			final boolean serialiseTout)
			throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException,
			NotImplementedSerializeException, IOException, MarshallExeption {
		final Type[] types = fieldInformations.getParametreType();
		Type genericTypeKey = Object.class;
		Type genericTypeValue = Object.class;
		if (types != null && types.length > 1) {
			genericTypeKey = types[0];
			genericTypeValue = types[1];
		}
		final String clefKey = genericTypeKey instanceof Class ? ((Class<?>) genericTypeKey).getSimpleName() : "Key";
		final String clefValue = genericTypeValue instanceof Class ? ((Class<?>) genericTypeValue).getSimpleName()
				: "Value";
		final FakeChamp fakeChampKey = new FakeChamp(clefKey, genericTypeKey, fieldInformations.getRelation(),
				fieldInformations.getAnnotations());
		final FakeChamp fakeChampValue = new FakeChamp(clefValue, genericTypeValue, fieldInformations.getRelation(),
				fieldInformations.getAnnotations());

		final Map<?, ?> map = obj;
		final Deque<Comportement> tmp = new ArrayDeque<>();
		for (final Entry<?, ?> entry : map.entrySet()) {
			tmp.push(traiteChamp(marshaller, entry.getKey(), fakeChampKey));
			tmp.push(traiteChamp(marshaller, entry.getValue(), fakeChampValue));
		}
		pushComportements(marshaller, tmp);
	}

	@Override
	protected void pushComportementParticulier(final Marshaller marshaller, final Map obj, final String nomBalise,
			final FieldInformations fieldInformations) {
		if (!obj.isEmpty()) {
			pushComportement(marshaller, newComportementFermeBalise(nomBalise));
			pushComportement(marshaller, newComportementOuvreBaliseEtEcritValeur(obj, nomBalise, fieldInformations));
		} else
			pushComportement(marshaller, newComportementOuvreEtFermeBalise(obj, nomBalise, fieldInformations));
	}
}
