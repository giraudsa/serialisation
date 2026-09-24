package giraudsa.marshall.deserialisation;

import java.io.IOException;
import java.util.IdentityHashMap;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import giraudsa.marshall.exception.EntityManagerImplementationException;
import giraudsa.marshall.exception.FabriqueInstantiationException;
import giraudsa.marshall.exception.InstanciationException;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.exception.SetValueException;
import utils.Constants;
import utils.EntityManager;
import utils.TypeExtension;

public abstract class Unmarshaller<T> {
	/** Class.forName est coûteux : les classes déjà résolues sont gardées par nom. */
	private static final Map<String, Class<?>> classesParNom = new ConcurrentHashMap<>();

	protected static Class<?> getClasse(final String nom) throws ClassNotFoundException {
		Class<?> classe = classesParNom.get(nom);
		if (classe == null) {
			classe = Class.forName(nom);
			classesParNom.put(nom, classe);
		}
		return classe;
	}

	protected static Class<?> getTypeDepuisNom(final String smallNameType) throws ClassNotFoundException {
		return getClasse(Constants.getNameType(smallNameType));
	}

	protected CacheObject cacheObject;
	/** créée à la demande : seuls les objets à faux id s'en servent. */
	private Map<Object, UUID> dicoObjToFakeId;
	protected final EntityManager entity;
	private final Fabrique fabrique;
	protected T obj;

	protected final Deque<ActionAbstrait<?>> pileAction = new ArrayDeque<>();

	protected Unmarshaller(final EntityManager entity) throws FabriqueInstantiationException {
		this.entity = entity;
		fabrique = Fabrique.getInstance();
		cacheObject = new CacheEmpty();
	}

	private <U> ActionAbstrait<?> choseAction(final Class<U> type) throws NotImplementedSerializeException {
		return choisiAction(getdicoTypeToAction(), type);
	}

	/** Choisit l'action d'un type à partir de sa famille (enum, map, date, collection...) et la mémorise. */
	protected static ActionAbstrait<?> choisiAction(final Map<Class<?>, ActionAbstrait<?>> actions,
			final Class<?> type) throws NotImplementedSerializeException {
		ActionAbstrait<?> behavior;
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
		behavior = actions.get(genericType);
		if (behavior == null)
			throw new NotImplementedSerializeException("not implemented: " + type);
		actions.put(type, behavior);
		return behavior;
	}

	protected void construitObjet(final ActionAbstrait<?> action)
			throws EntityManagerImplementationException, InstanciationException, SetValueException {
		action.construitObjet();
	}

	protected <W> W createInstance(final Class<W> type) throws InstanciationException {
		return fabrique.newObject(type);
	}

	public void dispose() throws IOException {
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected <U> ActionAbstrait getAction(final Class<U> type) throws NotImplementedSerializeException {
		final Map<Class<?>, ActionAbstrait<?>> actions = getdicoTypeToAction();
		ActionAbstrait behavior = null;
		if (type != null) {
			behavior = actions.get(type);
			if (behavior == null)
				behavior = choseAction(type);
			return behavior.getNewInstance(type, this);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	protected <U> ActionAbstrait<U> getActionEnCours() {
		if (pileAction.isEmpty())
			return null;
		return (ActionAbstrait<U>) pileAction.peek();
	}

	protected Map<Object, UUID> getDicoObjToFakeId() {
		if (dicoObjToFakeId == null)
			dicoObjToFakeId = new IdentityHashMap<>();
		return dicoObjToFakeId;
	}

	protected abstract Map<Class<?>, ActionAbstrait<?>> getdicoTypeToAction();

	protected EntityManager getEntityManager() {
		return entity;
	}

	protected <W> W getObject(final String id, final Class<W> type)
			throws EntityManagerImplementationException, InstanciationException {
		if (id == null) {
			final W ret = newInstance(type);
			if ((type != void.class || type != Void.class) && ret == null)
				throw new EntityManagerImplementationException(
						"Le contrat d'interface n'est pas remplie, l'objet récupéré est null pour le type " + type,
						new NullPointerException());
			return ret;
		}
		W objet = cacheObject.getObject(type, id);
		if (objet == null) {
			if (entity != null) {
				objet = entity.findObjectOrCreate(id, type);
				if ((type != void.class || type != Void.class) && objet == null)
					throw new EntityManagerImplementationException(
							"Le contrat d'interface n'est pas remplie, l'objet récupéré est null pour le type " + type,
							new NullPointerException());
			} else
				objet = newInstance(type);
			if (objet != null)
				cacheObject.addObject(objet, id);
		}
		return objet;
	}

	protected Object getObjet(final ActionAbstrait<?> action) {
		return action.getObjet();
	}

	protected <W> void integreObjet(final ActionAbstrait<?> action, final String nom, final W objet)
			throws IllegalAccessException, EntityManagerImplementationException, InstanciationException,
			SetValueException {
		action.integreObjet(nom, objet);
	}

	protected <W> W newInstance(final Class<W> type) throws InstanciationException {
		return fabrique.newObject(type);
	}

	protected void rempliData(final ActionAbstrait<?> action, final String donnees) throws InstanciationException {
		action.rempliData(donnees);

	}

	protected void setCache(final boolean isIdUniversel) {
		cacheObject = isIdUniversel ? new CacheIdUniversel() : new CacheIdNonUniversel();
	}
}
