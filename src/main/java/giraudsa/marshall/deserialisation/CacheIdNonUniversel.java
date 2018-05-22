package giraudsa.marshall.deserialisation;

import utils.BiHashMap;

public class CacheIdNonUniversel implements CacheObject {

	private final BiHashMap<String, Class<?>, Object> dicoIdAndClassToObject = new BiHashMap<>();

	@Override
	public <U> void addObject(final U object, final String id) {
		dicoIdAndClassToObject.put(id, object.getClass(), object);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <U> U getObject(final Class<U> clazz, final String id) {
		return (U) dicoIdAndClassToObject.get(id, clazz);
	}

}
