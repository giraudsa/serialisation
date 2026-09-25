package giraudsa.marshall.serialisation;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.UUID;

import giraudsa.marshall.exception.MarshallExeption;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.ActionAbstrait.Comportement;
import giraudsa.marshall.strategie.StrategieDeSerialisation;
import utils.Constants;
import utils.EntityManager;
import utils.IdentiteIntMap;
import utils.champ.FieldInformations;
import utils.TypeExtension;

public abstract class Marshaller {

	@SuppressWarnings("rawtypes")
	protected Deque<Comportement> aFaire = new ArrayDeque<>();
	// comparaison par identité : deux objets distincts mais égaux au sens de
	// equals() sont deux noeuds différents du graphe.
	// tables créées à la demande : le binaire ne s'en sert presque jamais, leur allocation pèse sur les petits graphes
	/** tables d'identité réutilisées d'une sérialisation à l'autre sur un même thread (voir rendTables). */
	private static final ThreadLocal<IdentiteIntMap[]> TABLES_LIBRES = ThreadLocal
			.withInitial(() -> new IdentiteIntMap[2]);

	private static IdentiteIntMap prendTable() {
		final IdentiteIntMap[] libres = TABLES_LIBRES.get();
		for (int i = 0; i < libres.length; i++)
			if (libres[i] != null) {
				final IdentiteIntMap table = libres[i];
				libres[i] = null;
				return table;
			}
		return new IdentiteIntMap(1024);
	}

	private static void rend(final IdentiteIntMap table) {
		table.vide(); // ne retient pas les objets sérialisés
		final IdentiteIntMap[] libres = TABLES_LIBRES.get();
		for (int i = 0; i < libres.length; i++)
			if (libres[i] == null) {
				libres[i] = table;
				return;
			}
	}

	/** Rend les tables d'identité pour la sérialisation suivante ; à appeler en fin de sérialisation. */
	protected void rendTables() {
		if (etats != null) {
			rend(etats);
			etats = null;
		}
	}

	// ensembles par identité (table à adressage ouvert : ni entrée allouée ni boxing)
	/** état de chaque objet rencontré : bits DEJA_VU et TOTALEMENT_SERIALISE (une seule recherche par accès). */
	private IdentiteIntMap etats;
	private static final int DEJA_VU = 1;
	private static final int TOTALEMENT_SERIALISE = 2;
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
		return etats != null && (etats.get(obj) & TOTALEMENT_SERIALISE) != 0; // ABSENT n'a aucun des deux bits
	}

	protected <T> boolean isDejaVu(final T obj) {
		return etats != null && (etats.get(obj) & DEJA_VU) != 0;
	}

	protected <T> void marshall(final T value, final FieldInformations fieldInformations)
			throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException,
			IOException, NotImplementedSerializeException, MarshallExeption {
		final var action = getAction(value);
		action.marshall(this, value, fieldInformations);
	}

	protected <T> void setDejaTotalementSerialise(final T obj) {
		if (etats == null)
			etats = prendTable();
		etats.ou(obj, TOTALEMENT_SERIALISE);
	}

	/** Marque l'objet déjà vu. @return true s'il était déjà totalement sérialisé (une seule recherche). */
	protected <T> boolean marqueVu(final T obj) {
		if (etats == null)
			etats = prendTable();
		final int precedent = etats.ou(obj, DEJA_VU);
		return precedent != IdentiteIntMap.ABSENT && (precedent & TOTALEMENT_SERIALISE) != 0;
	}

	protected <T> void setDejaVu(final T obj) {
		if (etats == null)
			etats = prendTable();
		etats.ou(obj, DEJA_VU);
	}
}
