package giraudsa.marshall.deserialisation.binary.actions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.ActionBinary;
import giraudsa.marshall.deserialisation.binary.BinaryUnmarshaller;
import giraudsa.marshall.exception.EntityManagerImplementationException;
import giraudsa.marshall.exception.InstanciationException;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.exception.SetValueException;
import giraudsa.marshall.exception.UnmarshallExeption;
import utils.TypeExtension;
import utils.champ.Champ;

public class ActionBinaryObject<O extends Object> extends ActionBinary<O> {
	public static ActionAbstrait<Object> getInstance() {
		return new ActionBinaryObject<>(Object.class, null);
	}

	private Champ champEnAttente = null;
	private Champ champId = null;
	private Iterator<Champ> iteratorChamp = null;

	private List<Champ> listeChamps = null;

	private ActionBinaryObject(final Class<O> type, final BinaryUnmarshaller<?> b) {
		super(type, b);
	}

	private boolean deserialiseId(final boolean isDejaVu) {
		if (champId.isFakeId())
			return false;
		return !isDejaVu;

	}

	@Override
	protected void deserialisePariellement()
			throws ClassNotFoundException, NotImplementedSerializeException, IOException, UnmarshallExeption,
			InstanciationException, IllegalAccessException, EntityManagerImplementationException, SetValueException {
		if (champEnAttente != null) {
			if (champEnAttente != TypeExtension.getChampId(type))
				setDejaTotalementDeSerialise();
			litObject(champEnAttente);
		} else
			exporteObject();
	}

	private boolean deserialiseToutSaufId() {
		return strategieDeSerialiseTout() && !isDejaTotalementDeSerialise();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public <U extends O> ActionAbstrait<U> getNewInstance(final Class<U> type, final Unmarshaller unmarshaller) {
		return new ActionBinaryObject<>(type, (BinaryUnmarshaller<?>) unmarshaller);
	}

	@Override
	protected void initialise() throws InstanciationException {
		champId = TypeExtension.getChampId(type);
		final boolean isDejaVu = isDejaVu();
		if (isDejaVu)
			obj = getObjet();
		else if (champId.isFakeId()) {
			obj = newInstanceOfType();
			stockeObjetId();
		}
		final boolean deserialiseToutSaufId = deserialiseToutSaufId();
		final boolean deserialiseId = deserialiseId(isDejaVu);

		initialiseListeChamps(deserialiseToutSaufId, deserialiseId);
		if (listeChamps != null && !listeChamps.isEmpty()) {
			iteratorChamp = listeChamps.iterator();
			champEnAttente = iteratorChamp.next();
		}
	}

	private void initialiseListeChamps(final boolean deserialiseToutSaufId, final boolean deserialiseId) {
		listeChamps = new ArrayList<>();
		if (deserialiseId)
			listeChamps.add(champId);
		if (deserialiseToutSaufId)
			for (final Champ champ : TypeExtension.getSerializableFields(type))
				if (champ != champId)
					listeChamps.add(champ);
	}

	@Override
	protected void integreObjet(final String nom, final Object objet) throws EntityManagerImplementationException,
			InstanciationException, SetValueException, IllegalAccessException {
		if (champEnAttente == champId) {
			final String id = objet.toString();
			obj = getObject(id, type);
			stockeObjetId();
		}
		if (champEnAttente.get(obj, getDicoObjToFakeId(), getEntityManager()) != objet && !champEnAttente.isFakeId())
			champEnAttente.set(obj, objet, getDicoObjToFakeId());
		if (iteratorChamp.hasNext())
			champEnAttente = iteratorChamp.next();
		else
			exporteObject();
	}
}