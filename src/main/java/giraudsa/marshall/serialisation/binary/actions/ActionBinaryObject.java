package giraudsa.marshall.serialisation.binary.actions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

import giraudsa.marshall.exception.MarshallExeption;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.binary.ActionBinary;
import utils.TypeExtension;
import utils.champ.Champ;
import utils.champ.FieldInformations;

public class ActionBinaryObject extends ActionBinary<Object> {

	public ActionBinaryObject() {
		super();
	}

	@Override
	protected void ecritValeur(final Marshaller marshaller, final Object objetASerialiser,
			final FieldInformations fieldInformations, final boolean isDejaVu)
			throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException,
			NotImplementedSerializeException, IOException, MarshallExeption {
		final Deque<Comportement> tmp = new ArrayDeque<>();

		final boolean serialiseToutSaufId = serialiseToutSaufId(marshaller, objetASerialiser, fieldInformations);
		final boolean serialiseId = serialiseId(objetASerialiser, isDejaVu);

		if (serialiseToutSaufId)
			setDejaTotalementSerialise(marshaller, objetASerialiser);

		final List<Champ> champs = getListeChamp(objetASerialiser, serialiseId, serialiseToutSaufId);
		for (final Champ champ : champs) {
			final Comportement comportement = traiteChamp(marshaller, objetASerialiser, champ);
			if (comportement != null)
				tmp.push(comportement);
		}
		pushComportements(marshaller, tmp);
	}

	private List<Champ> getListeChamp(final Object objetASerialiser, final boolean serialiseId,
			final boolean serialiseToutSaufId) {
		final Class<?> type = objetASerialiser.getClass();
		if (serialiseToutSaufId)
			return serialiseId ? TypeExtension.getSerializableFields(type)
					: TypeExtension.getSerializableFieldsSaufId(type);
		return serialiseId ? Collections.singletonList(TypeExtension.getChampId(type)) : Collections.emptyList();
	}

	private boolean serialiseId(final Object objetASerialiser, final boolean isDejaVu) {
		final boolean isFakeId = TypeExtension.getChampId(objetASerialiser.getClass()).isFakeId();
		if (isFakeId)
			return false;
		return !isDejaVu;
	}

	private boolean serialiseToutSaufId(final Marshaller marshaller, final Object objetASerialiser,
			final FieldInformations fieldInformations) {
		return strategieSerialiseTout(marshaller, fieldInformations)
				&& !isDejaTotalementSerialise(marshaller, objetASerialiser);
	}

}
