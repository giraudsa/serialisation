package giraudsa.marshall.serialisation.text.json.actions;

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
import giraudsa.marshall.serialisation.text.json.ActionJson;
import utils.Constants;
import utils.champ.FakeChamp;
import utils.champ.FieldInformations;

@SuppressWarnings("rawtypes")
public class ActionJsonDictionary extends ActionJson<Map> {

	public ActionJsonDictionary() {
		super();
	}

	@Override
	protected void clotureObject(final Marshaller marshaller, final Map obj, final boolean typeDevinable)
			throws IOException {
		if (typeDevinable)
			fermeCrochet(marshaller, !obj.isEmpty());
		else {
			fermeCrochet(marshaller, !obj.isEmpty());
			fermeAccolade(marshaller);
		}
	}

	@Override
	protected boolean commenceObject(final Marshaller marshaller, final Map obj, final boolean typeDevinable)
			throws IOException {
		if (typeDevinable)
			ouvreCrochet(marshaller);
		else {
			ouvreAccolade(marshaller);
			ecritType(marshaller, obj);
			writeSeparator(marshaller);
			ecritClef(marshaller, Constants.VALEUR);
			ouvreCrochet(marshaller);
		}
		return false;
	}

	@Override
	protected void ecritValeur(final Marshaller marshaller, final Map obj, final FieldInformations fi,
			boolean ecrisSeparateur) throws InstantiationException, InvocationTargetException, NoSuchMethodException,
			IllegalAccessException, NotImplementedSerializeException, IOException, MarshallExeption {
		final Type[] types = fi.getParametreType();
		Type genericTypeKey = Object.class;
		Type genericTypeValue = Object.class;
		if (types != null && types.length > 1) {
			genericTypeKey = types[0];
			genericTypeValue = types[1];
		}
		final FakeChamp fakeChampKey = new FakeChamp(null, genericTypeKey, fi.getRelation(), fi.getAnnotations());
		final FakeChamp fakeChampValue = new FakeChamp(null, genericTypeValue, fi.getRelation(), fi.getAnnotations());

		final Map<?, ?> map = obj;
		final Deque<Comportement> tmp = new ArrayDeque<>();
		for (final Entry<?, ?> entry : map.entrySet()) {
			tmp.push(traiteChamp(marshaller, entry.getKey(), fakeChampKey, ecrisSeparateur));
			ecrisSeparateur = true;
			tmp.push(traiteChamp(marshaller, entry.getValue(), fakeChampValue));
		}
		pushComportements(marshaller, tmp);// on remet dans le bon ordre
	}
}
