package giraudsa.marshall.serialisation.binary.actions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayDeque;
import java.util.ArrayList;
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
		final var tmp = new ArrayDeque<Comportement>();

		final var serialiseToutSaufId = serialiseToutSaufId(marshaller, objetASerialiser, fieldInformations);
		final var serialiseId = serialiseId(objetASerialiser, isDejaVu);

		if (serialiseToutSaufId) {
			setDejaTotalementSerialise(marshaller, objetASerialiser);
		}

		final var champs = getListeChamp(objetASerialiser, serialiseId, serialiseToutSaufId);
		for (final var champ : champs) {
			final var comportement = traiteChamp(marshaller, objetASerialiser, champ);
			if (comportement != null) {
				tmp.push(comportement);
			}
		}
		pushComportements(marshaller, tmp);
	}

	private List<Champ> getListeChamp(final Object objetASerialiser, final boolean serialiseId,
			final boolean serialiseToutSaufId) {
		final var ret = new ArrayList<Champ>();
		final var champId = TypeExtension.getChampId(objetASerialiser.getClass());
		final var champs = TypeExtension.getSerializableFields(objetASerialiser.getClass());
		for (final var champ : champs) {
			if ((champ == champId && serialiseId) || (champ != champId && serialiseToutSaufId)) {
				ret.add(champ);
			}
		}
		return ret;
	}

	private boolean serialiseId(final Object objetASerialiser, final boolean isDejaVu) {
		final var isFakeId = TypeExtension.getChampId(objetASerialiser.getClass()).isFakeId();
		return !isFakeId && !isDejaVu;
	}

	private boolean serialiseToutSaufId(final Marshaller marshaller, final Object objetASerialiser,
			final FieldInformations fieldInformations) {
		return strategieSerialiseTout(marshaller, fieldInformations)
				&& !isDejaTotalementSerialise(marshaller, objetASerialiser);
	}

}
