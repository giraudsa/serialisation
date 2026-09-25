package giraudsa.marshall.deserialisation;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
	/**
	 * sun.misc.Unsafe.allocateInstance(Class) : alloue l'objet sans exécuter de constructeur ni d'initialiseur, comme
	 * le constructeur de sérialisation, mais par une intrinsèque du JIT (le constructeur de sérialisation passe par un
	 * MethodHandle non constant, parfois très mal compilé). Cette méthode n'est pas concernée par la dépréciation des
	 * accès mémoire d'Unsafe (JEP 471). null si indisponible : on garde alors le constructeur de sérialisation.
	 */
	private static final MethodHandle ALLOCATION = chercheAllocation();

	private static MethodHandle chercheAllocation() {
		try {
			final Class<?> classeUnsafe = Class.forName("sun.misc.Unsafe");
			final Field champ = classeUnsafe.getDeclaredField("theUnsafe");
			champ.setAccessible(true);
			final Object unsafe = champ.get(null);
			return MethodHandles.lookup()
					.findVirtual(classeUnsafe, "allocateInstance", MethodType.methodType(Object.class, Class.class))
					.bindTo(unsafe);
		} catch (final ReflectiveOperationException | RuntimeException e) {
			return null;
		}
	}

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
	// partagé entre threads (singleton) ; ClassValue : plus rapide qu'une map concurrente
	private final ClassValue<Constructor<?>> constructeurs = new ClassValue<>() {
		@Override
		protected Constructor<?> computeValue(final Class<?> type) {
			return creeConstructeur(type);
		}
	};
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
			return (Constructor<T>) constructeurs.get(type);
		} catch (final IllegalStateException e) {
			throw new ConstructorException("impossible de creer le constructeur pour le type " + type.getName(),
					(Exception) e.getCause());
		}
	}

	private Constructor<?> creeConstructeur(final Class<?> type) {
		try {
			final Constructor<?> constr = (Constructor<?>) newConstructorForSerializationMethod.invoke(reflectionFactory,
					type, constructeurObject);
			constr.setAccessible(true);
			return constr;
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new IllegalStateException(e);
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
	@SuppressWarnings("unchecked")
	public <T> T newObject(final Class<T> type) throws InstanciationException {
		if (type == void.class || type == Void.class)
			return null;
		if (ALLOCATION != null)
			try {
				return (T) ALLOCATION.invokeExact(type);
			} catch (final InstantiationException e) {
				throw new InstanciationException("impossible d'instancier le type " + type.getName(), e);
			} catch (final Throwable e) { // NOSONAR : invokeExact déclare Throwable
				throw new InstanciationException("impossible d'instancier le type " + type.getName(),
						e instanceof Exception ? (Exception) e : new IllegalStateException(e));
			}
		try {
			return getConstructor(type).newInstance(noArgument);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| ConstructorException e) {
			throw new InstanciationException("impossible d'instancier le type " + type.getName(), e);
		}
	}

}
