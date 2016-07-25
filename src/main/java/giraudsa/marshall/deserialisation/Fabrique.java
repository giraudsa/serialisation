package giraudsa.marshall.deserialisation;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import giraudsa.marshall.exception.UnmarshallExeption;

public class Fabrique {
	private static final Object[] noArgument = new Object[0];
	private static Fabrique instance;

	private final Object reflectionFactory; //instance de sun.reflect.ReflectionFactory
	private final Method newConstructorForSerializationMethod; //methode public Constructor newConstructorForSerialization(Class classToInstantiate, Constructor constructorToCall)
	private final Constructor<Object> constructeurObject;//constructeur par défaut de la classe Object
	private final Map<Class<?>, Object> dicoClassToConstructeur = new HashMap<>();

	private Fabrique() throws UnmarshallExeption{
		try {
			Class<?> reflectionFactoryClazz = Class.forName("sun.reflect.ReflectionFactory");
			Method method = reflectionFactoryClazz.getDeclaredMethod("getReflectionFactory");
			reflectionFactory = method.invoke(null);
			newConstructorForSerializationMethod = reflectionFactoryClazz.getDeclaredMethod("newConstructorForSerialization", Class.class, Constructor.class);
			constructeurObject = Object.class.getConstructor(new Class[0]);
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new UnmarshallExeption("impossible de créer la fabrique", e);	
		}
	}

	static synchronized Fabrique getInstance() throws UnmarshallExeption{
		if(instance == null)
			instance =  new Fabrique();
		return instance;
	}

	<T> T newObject(Class<T> type) throws UnmarshallExeption{
		try {
			return (T)getConstructor(type).newInstance(noArgument);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new UnmarshallExeption("impossible d'instancier le type " + type.getName(), e);
		}
	}


	//Equivalent de 
	//return reflectionFactory.newConstructorForSerialization(type, constructor);
	@SuppressWarnings("unchecked")
	private  <T> Constructor<T> getConstructor(Class<T> type ) throws UnmarshallExeption{
		try {
			if(!dicoClassToConstructeur.containsKey(type)){
				
				Object constr = newConstructorForSerializationMethod.invoke(reflectionFactory, type, constructeurObject);
				dicoClassToConstructeur.put(type, constr);
				((Constructor<?>)constr).setAccessible(true);
			}
			return (Constructor<T>)dicoClassToConstructeur.get(type);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new UnmarshallExeption("impossible de creer le constructeur pour le type " + type.getName(), e);
		}
	}

}
