package giraudsa.marshall.serialisation.text.json.actions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import giraudsa.marshall.exception.MarshallExeption;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.text.json.ActionJson;
import utils.TypeExtension;
import utils.champ.Champ;
import utils.champ.FieldInformations;

public class ActionJsonObject extends ActionJson<Object> {

	public ActionJsonObject() {
		super();
	}

	@Override
	protected void clotureObject(final Marshaller marshaller, final Object obj, final boolean typeDevinable)
			throws IOException {
		fermeAccolade(marshaller);
	}

	@Override
	protected boolean commenceObject(final Marshaller marshaller, final Object obj, final boolean typeDevinable)
			throws IOException {
		ouvreAccolade(marshaller);
		if (!typeDevinable) {
			ecritType(marshaller, obj);
			return true;
		}
		return false;
	}

	@Override
	protected void ecritValeur(final Marshaller marshaller, final Object obj, final FieldInformations fieldInformations,
			final boolean ecrisSeparateur)
			throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException,
			NotImplementedSerializeException, IOException, MarshallExeption {
		final Class<?> typeObj = obj.getClass();
		final List<Champ> champs = TypeExtension.getSerializableFields(typeObj);
		final Champ champId = TypeExtension.getChampId(typeObj);
		final boolean serialiseTout = serialiseTout(marshaller, obj, fieldInformations);
		setDejaVu(marshaller, obj);
		if (!serialiseTout) {
			pushComportement(marshaller, traiteChamp(marshaller, obj, champId, ecrisSeparateur));
			return;
		}
		setDejaTotalementSerialise(marshaller, obj);
		final Deque<Comportement> tmp = new ArrayDeque<>();
		boolean virgule = ecrisSeparateur;
		for (final Champ champ : champs) {
			final Comportement comportement = traiteChamp(marshaller, obj, champ, virgule);
			virgule = true;
			if (comportement != null)
				tmp.push(comportement);
		}
		pushComportements(marshaller, tmp);
	}
}
