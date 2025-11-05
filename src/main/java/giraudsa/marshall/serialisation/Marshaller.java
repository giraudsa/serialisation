package giraudsa.marshall.serialisation;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import giraudsa.marshall.exception.MarshallExeption;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.ActionAbstrait.Comportement;
import giraudsa.marshall.strategie.StrategieDeSerialisation;
import utils.Constants;
import utils.EntityManager;
import utils.champ.FieldInformations;

public abstract class Marshaller {

	@SuppressWarnings("rawtypes")
	protected Deque<Comportement> aFaire = new ArrayDeque<>();
	protected Set<Object> dejaTotalementSerialise = new HashSet<>();
	private final Set<Object> dejaVu = new HashSet<>();
	private final Map<Object, UUID> dicoObjToFakeId = new HashMap<>();
	private final EntityManager entityManager;
	////// ATTRIBUT
	protected int profondeur;
	protected StrategieDeSerialisation strategie;

	////// Constructeur
	protected Marshaller(final StrategieDeSerialisation strategie, final EntityManager entityManager) {
		this.strategie = strategie;
		this.entityManager = entityManager;
	}

	void augmenteProdondeur() {
		++profondeur;
	}

	@SuppressWarnings("rawtypes")
	private <T> ActionAbstrait choisiAction(final Class<T> type) throws NotImplementedSerializeException {
		final Map<Class<?>, ActionAbstrait<?>> dicoTypeToAction = getDicoTypeToAction();

		final Class<?> genericType = determineGenericType(type);

		final ActionAbstrait action = dicoTypeToAction.get(genericType);
		dicoTypeToAction.put(type, action);
		if (action == null)
			throw new NotImplementedSerializeException("not implemented: " + type);
		return action;
	}

	private <T> Class<?> determineGenericType(final Class<T> type) {
		if (type.isEnum())
			return Constants.enumType;
		if (Constants.dictionaryType.isAssignableFrom(type))
			return Constants.dictionaryType;
		if (Constants.dateType.isAssignableFrom(type))
			return Constants.dateType;
		if (Constants.collectionType.isAssignableFrom(type))
			return Constants.collectionType;
		if (type.isArray())
			return Constants.arrayType;
		if (Constants.inetAdress.isAssignableFrom(type))
			return Constants.inetAdress;
		if (Constants.calendarType.isAssignableFrom(type))
			return Constants.calendarType;
		if (type.getPackage() == null || !type.getPackage().getName().startsWith("System"))
			return Constants.objectType;
		return type;
	}

	protected void deserialisePile() throws InstantiationException, IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, IOException, NotImplementedSerializeException, MarshallExeption {
		aFaire.pop().evalue(this);
	}

	void diminueProfondeur() {
		--profondeur;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected <T> ActionAbstrait getAction(final T obj) throws NotImplementedSerializeException {
		final Map<Class<?>, ActionAbstrait<?>> dicoTypeToAction = getDicoTypeToAction();
		ActionAbstrait action;
		if (obj == null)
			action = dicoTypeToAction.get(void.class);
		else {
			final Class<T> type = (Class<T>) obj.getClass();
			action = dicoTypeToAction.get(type);
			if (action == null)
				action = choisiAction(type);
		}
		return action;
	}

	protected Map<Object, UUID> getDicoObjToFakeId() {
		return dicoObjToFakeId;
	}

	protected abstract Map<Class<?>, ActionAbstrait<?>> getDicoTypeToAction();

	public EntityManager getEntityManager() {
		return entityManager;
	}

	int getProfondeur() {
		return profondeur;
	}

	StrategieDeSerialisation getStrategie() {
		return strategie;
	}

	protected <T> boolean isDejaTotalementSerialise(final T obj) {
		return dejaTotalementSerialise.contains(obj);
	}

	protected <T> boolean isDejaVu(final T obj) {
		return dejaVu.contains(obj);
	}

	protected <T> void marshall(final T value, final FieldInformations fieldInformations)
			throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException,
			IOException, NotImplementedSerializeException, MarshallExeption {
		final ActionAbstrait<?> action = getAction(value);
		action.marshall(this, value, fieldInformations);
	}

	protected <T> void setDejaTotalementSerialise(final T obj) {
		dejaTotalementSerialise.add(obj);
	}

	protected <T> void setDejaVu(final T obj) {
		dejaVu.add(obj);
	}
}
