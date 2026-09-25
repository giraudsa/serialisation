package io.github.giraudsa.fidelis.deserialisation;

public interface CacheObject {
	<U> void addObject(U object, String id);

	<U> U getObject(Class<U> clazz, String id);

}
