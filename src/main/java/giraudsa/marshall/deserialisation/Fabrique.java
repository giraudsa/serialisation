package giraudsa.marshall.deserialisation;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import giraudsa.marshall.exception.ConstructorException;
import giraudsa.marshall.exception.FabriqueInstantiationException;
import giraudsa.marshall.exception.InstanciationException;

/**
 * Classe avec un singleton permettant l'instanciation d'objets ex-nihilot sans
 * passer par un constructeur.
 *
 * @author giraudsa
 *
 */
public class Fabrique {
	private static volatile Fabrique instance;
	private static final Object[] noArgument = new Object[0];

	public static Fabrique getInstance() throws FabriqueInstantiationException {
		Fabrique res = instance;
		if (res == null)
			synchronized (Fabrique.class) {
				res = instance;
				if (res == null) {
					res = new Fabrique();
					instance = res;
				}
			}
		return res;
	}

	private final Constructor<Object> constructeurObject;// constructeur par défaut de la classe Object
	// partagé entre threads (singleton)
	private final Map<Class<?>, Constructor<?>> dicoClassToConstructeur = new ConcurrentHashMap<>();
	private final Method newConstructorForSerializationMethod; // methode public Constructor
																// newConstructorForSerialization(Class
																// classToInstantiate, Constructor constructorToCall)

	private final Object reflectionFactory; // instance de sun.reflect.ReflectionFactory

	private Fabrique() throws FabriqueInstantiationException {
		try {
			final Class<?> reflectionFactoryClazz = Class.forName("sun.reflect.ReflectionFactory");
			final Method method = reflectionFactoryClazz.getDeclaredMethod("getReflectionFactory");
			reflectionFactory = method.invoke(null);
			newConstructorForSerializationMethod = reflectionFactoryClazz
					.getDeclaredMethod("newConstructorForSerialization", Class.class, Constructor.class);
			constructeurObject = Object.class.getConstructor(new Class[0]);
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			throw new FabriqueInstantiationException("impossible de créer la fabrique", e);
		}
	}

	// Equivalent de
	// "return reflectionFactory.newConstructorForSerialization(type, constructor);"
	@SuppressWarnings("unchecked")
	private <T> Constructor<T> getConstructor(final Class<T> type) throws ConstructorException {
		Constructor<?> constr = dicoClassToConstructeur.get(type);
		if (constr == null) {
			try {
				constr = (Constructor<?>) newConstructorForSerializationMethod.invoke(reflectionFactory, type,
						constructeurObject);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new ConstructorException("impossible de creer le constructeur pour le type " + type.getName(), e);
			}
			constr.setAccessible(true);
			final Constructor<?> existant = dicoClassToConstructeur.putIfAbsent(type, constr);
			if (existant != null)
				constr = existant;
		}
		return (Constructor<T>) constr;
	}

	/**
	 * instancie un objet ex-nihilot sans passer par un constructeur et donc sans
	 * effet de bord...
	 * 
	 * @param type
	 * @return
	 * @throws InstanciationException
	 */
	public <T> T newObject(final Class<T> type) throws InstanciationException {
		try {
			if (type == void.class || type == Void.class)
				return null;
			return getConstructor(type).newInstance(noArgument);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| ConstructorException e) {
			throw new InstanciationException("impossible d'instancier le type " + type.getName(), e);
		}
	}

}
