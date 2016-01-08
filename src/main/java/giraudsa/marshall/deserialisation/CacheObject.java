package giraudsa.marshall.deserialisation;

public interface CacheObject {
	<U> U getObject(Class<U> clazz, String id);
	<U> void addObject(U object, String id);

}
