package giraudsa.marshall.serialisation;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Deque;
import java.util.Map;
import java.util.UUID;

import giraudsa.marshall.exception.MarshallExeption;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.strategie.StrategieDeSerialisation;
import utils.EntityManager;
import utils.champ.FakeChamp;
import utils.champ.FieldInformations;

public abstract class ActionAbstrait<T> {

	protected abstract class Comportement {
		protected abstract void evalue(Marshaller marshaller)
				throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException,
				NoSuchMethodException, NotImplementedSerializeException, MarshallExeption;
	}

	protected class ComportementMarshallValue extends Comportement {
		private final FieldInformations fieldInformations;
		private final Object value;
		private final boolean writeSeparateur;

		protected ComportementMarshallValue(final Object value, final FieldInformations fieldInformations,
				final boolean writeSeparateur) {
			super();
			this.value = value;
			this.fieldInformations = fieldInformations;
			this.writeSeparateur = writeSeparateur;
		}

		@Override
		protected void evalue(final Marshaller marshaller)
				throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException,
				NoSuchMethodException, NotImplementedSerializeException, MarshallExeption {
			if (writeSeparateur)
				writeSeparator(marshaller);
			marshaller.marshall(value, fieldInformations);

		}
	}

	protected ActionAbstrait() {
		super();
	}

	protected <V> boolean aTraiter(final V value, final FieldInformations fieldInformations) throws MarshallExeption {
		if (fieldInformations instanceof FakeChamp)
			return true;
		if (fieldInformations.isChampId() && value == null)
			throw new MarshallExeption("l'objet a un id null");
		return value != null;
	}

	protected void augmenteProdondeur(final Marshaller marshaller) {
		marshaller.augmenteProdondeur();
	}

	protected void diminueProfondeur(final Marshaller marshaller) {
		marshaller.diminueProfondeur();
	}

	protected Map<Object, UUID> getDicoObjToFakeId(final Marshaller marshaller) {
		return marshaller.getDicoObjToFakeId();
	}

	protected EntityManager getEntityManager(final Marshaller marshaller) {
		return marshaller.getEntityManager();
	}

	protected int getProfondeur(final Marshaller marshaller) {
		return marshaller.getProfondeur();
	}

	protected StrategieDeSerialisation getStrategie(final Marshaller marshaller) {
		return marshaller.getStrategie();
	}

	protected Class<?> getType(final T obj) {
		if (obj == null)
			return Void.class;
		return obj.getClass();
	}

	protected <U> boolean isDejaTotalementSerialise(final Marshaller marshaller, final U object) {
		return marshaller.isDejaTotalementSerialise(object);
	}

	protected <U> boolean isDejaVu(final Marshaller marshaller, final U objet) {
		return marshaller.isDejaVu(objet);
	}

	protected boolean isTypeDevinable(final Marshaller marshaller, final Object value,
			final FieldInformations fieldInformations) {
		if (value == null)
			return false;
		if (isDejaVu(marshaller, value) && isUniversalId(marshaller))
			return true;
		return fieldInformations.isTypeDevinable(value);
	}

	protected boolean isUniversalId(final Marshaller marshaller) {
		return true;
	}

	protected abstract void marshall(Marshaller marshaller, Object obj, FieldInformations fieldInformations)
			throws MarshallExeption;

	protected void pushComportement(final Marshaller marshaller, final Comportement comportement) {
		marshaller.aFaire.push(comportement);
	}

	protected void pushComportements(final Marshaller marshaller, final Deque<Comportement> comportements) {
		while (!comportements.isEmpty())
			pushComportement(marshaller, comportements.pop());
	}

	protected <U> void setDejaTotalementSerialise(final Marshaller marshaller, final U object) {
		marshaller.setDejaTotalementSerialise(object);
	}

	protected <U> void setDejaVu(final Marshaller marshaller, final U objet) {
		marshaller.setDejaVu(objet);
	}

	protected boolean strategieSerialiseTout(final Marshaller marshaller, final FieldInformations fieldInformations) {
		return getStrategie(marshaller).serialiseTout(getProfondeur(marshaller), fieldInformations);
	}

	protected Comportement traiteChamp(final Marshaller marshaller, final Object obj,
			final FieldInformations fieldInformations) throws IllegalAccessException, MarshallExeption {
		return traiteChamp(marshaller, obj, fieldInformations, true);
	}

	protected Comportement traiteChamp(final Marshaller marshaller, final Object obj,
			final FieldInformations fieldInformations, final boolean ecrisSeparateur)
			throws IllegalAccessException, MarshallExeption {
		final Object value = fieldInformations.get(obj, getDicoObjToFakeId(marshaller), getEntityManager(marshaller));
		if (aTraiter(value, fieldInformations))
			return new ComportementMarshallValue(value, fieldInformations, ecrisSeparateur);
		return null;
	}

	protected void writeSeparator(final Marshaller marshaller) throws IOException {
	}
}
