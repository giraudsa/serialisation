package giraudsa.marshall.deserialisation;

import java.util.HashMap;
import java.util.Map;

public class CacheIdUniversel implements CacheObject {

	private final Map<String, Object> dicoIdToObject = new HashMap<>();

	@Override
	public <U> void addObject(final U obj, final String id) {
		dicoIdToObject.put(id, obj);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <U> U getObject(final Class<U> clazz, final String id) {
		return (U) dicoIdToObject.get(id);
	}
}
