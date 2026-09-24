package giraudsa.marshall.serialisation;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Collections;
import java.util.IdentityHashMap;
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
import utils.TypeExtension;

public abstract class Marshaller {

	@SuppressWarnings("rawtypes")
	protected Deque<Comportement> aFaire = new ArrayDeque<>();
	// comparaison par identité : deux objets distincts mais égaux au sens de
	// equals() sont deux noeuds différents du graphe.
	// tables créées à la demande : le binaire ne s'en sert presque jamais, leur allocation pèse sur les petits graphes
	protected Set<Object> dejaTotalementSerialise;
	private Set<Object> dejaVu;
	private Map<Object, UUID> dicoObjToFakeId;
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
		return choisiAction(getDicoTypeToAction(), type);
	}

	/** Choisit l'action d'un type à partir de sa famille (enum, map, date, collection...) et la mémorise. */
	@SuppressWarnings("rawtypes")
	protected static ActionAbstrait choisiAction(final Map<Class<?>, ActionAbstrait<?>> dicoTypeToAction,
			final Class<?> type) throws NotImplementedSerializeException {
		ActionAbstrait action;
		Class<?> genericType = type;
		if (TypeExtension.isEnum(type))
			genericType = Constants.enumType;
		else if (Constants.dictionaryType.isAssignableFrom(type))
			genericType = Constants.dictionaryType;
		else if (Constants.dateType.isAssignableFrom(type))
			genericType = Constants.dateType;
		else if (Constants.collectionType.isAssignableFrom(type))
			genericType = Constants.collectionType;
		else if (type.isArray())
			genericType = Constants.arrayType;
		else if (Constants.inetAdress.isAssignableFrom(type))
			genericType = Constants.inetAdress;
		else if (Constants.calendarType.isAssignableFrom(type))
			genericType = Constants.calendarType;
		else if (type.getPackage() == null || !type.getPackage().getName().startsWith("System"))
			genericType = Constants.objectType;
		action = dicoTypeToAction.get(genericType);
		if (action == null)
			throw new NotImplementedSerializeException("not implemented: " + type);
		dicoTypeToAction.put(type, action);
		return action;
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
		final var dicoTypeToAction = getDicoTypeToAction();
		ActionAbstrait action;
		if (obj == null)
			action = dicoTypeToAction.get(void.class);
		else {
			final var type = (Class<T>) TypeExtension.getClasseASerialiser(obj);
			action = dicoTypeToAction.get(type);
			if (action == null)
				action = choisiAction(type);
		}
		return action;
	}

	protected Map<Object, UUID> getDicoObjToFakeId() {
		if (dicoObjToFakeId == null)
			dicoObjToFakeId = new IdentityHashMap<>();
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
		return dejaTotalementSerialise != null && dejaTotalementSerialise.contains(obj);
	}

	protected <T> boolean isDejaVu(final T obj) {
		return dejaVu != null && dejaVu.contains(obj);
	}

	protected <T> void marshall(final T value, final FieldInformations fieldInformations)
			throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException,
			IOException, NotImplementedSerializeException, MarshallExeption {
		final var action = getAction(value);
		action.marshall(this, value, fieldInformations);
	}

	protected <T> void setDejaTotalementSerialise(final T obj) {
		if (dejaTotalementSerialise == null)
			dejaTotalementSerialise = Collections.newSetFromMap(new IdentityHashMap<>());
		dejaTotalementSerialise.add(obj);
	}

	protected <T> void setDejaVu(final T obj) {
		if (dejaVu == null)
			dejaVu = Collections.newSetFromMap(new IdentityHashMap<>());
		dejaVu.add(obj);
	}
}
