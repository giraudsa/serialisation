package giraudsa.marshall.deserialisation;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

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
	private static Fabrique instance;
	private static final Object[] noArgument = new Object[0];

	public static synchronized Fabrique getInstance() throws FabriqueInstantiationException {
		if (instance == null)
			instance = new Fabrique();
		return instance;
	}

	private final Constructor<Object> constructeurObject;// constructeur par défaut de la classe Object
	private final Map<Class<?>, Object> dicoClassToConstructeur = new HashMap<>();
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
		try {
			if (!dicoClassToConstructeur.containsKey(type)) {

				final Object constr = newConstructorForSerializationMethod.invoke(reflectionFactory, type,
						constructeurObject);
				dicoClassToConstructeur.put(type, constr);
				((Constructor<?>) constr).setAccessible(true);
			}
			return (Constructor<T>) dicoClassToConstructeur.get(type);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new ConstructorException("impossible de creer le constructeur pour le type " + type.getName(), e);
		}
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
