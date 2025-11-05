package giraudsa.marshall.deserialisation;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import giraudsa.marshall.exception.EntityManagerImplementationException;
import giraudsa.marshall.exception.FabriqueInstantiationException;
import giraudsa.marshall.exception.InstanciationException;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.exception.SetValueException;
import utils.Constants;
import utils.EntityManager;

public abstract class Unmarshaller<T> {
	protected static Class<?> getTypeDepuisNom(final String smallNameType) throws ClassNotFoundException {
		return Class.forName(Constants.getNameType(smallNameType));
	}

	protected CacheObject cacheObject;
	private final Map<Object, UUID> dicoObjToFakeId = new HashMap<>();
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
		final Map<Class<?>, ActionAbstrait<?>> actions = getdicoTypeToAction();

		final Class<?> genericType = determineGenericType(type);

		final ActionAbstrait<?> behavior = actions.get(genericType);
		actions.put(type, behavior);
		if (behavior == null)
			throw new NotImplementedSerializeException("not implemented: " + type);
		return behavior;
	}

	private <U> Class<?> determineGenericType(final Class<U> type) {
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
		return dicoObjToFakeId;
	}

	protected abstract Map<Class<?>, ActionAbstrait<?>> getdicoTypeToAction();

	protected EntityManager getEntityManager() {
		return entity;
	}

	<W> W getObject(final String id, final Class<W> type)
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

	<W> W newInstance(final Class<W> type) throws InstanciationException {
		return fabrique.newObject(type);
	}

	protected void rempliData(final ActionAbstrait<?> action, final String donnees) throws InstanciationException {
		action.rempliData(donnees);

	}

	protected void setCache(final boolean isIdUniversel) {
		cacheObject = isIdUniversel ? new CacheIdUniversel() : new CacheIdNonUniversel();
	}
}
