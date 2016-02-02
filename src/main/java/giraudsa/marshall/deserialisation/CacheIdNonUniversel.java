package giraudsa.marshall.deserialisation;

import utils.BiHashMap;

public class CacheIdNonUniversel implements CacheObject {

	private BiHashMap<String, Class<?>, Object> dicoIdAndClassToObject = new BiHashMap<String, Class<?>, Object>();
	@SuppressWarnings("unchecked")

	public <U> U getObject(Class<U> clazz, String id) {
		return (U) dicoIdAndClassToObject.get(id, clazz);
	}


	public <U> void addObject(U object, String id) {
		dicoIdAndClassToObject.put(id, object.getClass(), object);
	}

}
