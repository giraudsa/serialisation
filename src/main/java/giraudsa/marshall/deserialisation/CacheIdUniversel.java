package giraudsa.marshall.deserialisation;

import java.util.HashMap;
import java.util.Map;

public class CacheIdUniversel implements CacheObject {

	private final Map<String, Object>  dicoIdToObject = new HashMap<String, Object>();
	@SuppressWarnings("unchecked")

	public <U> U getObject(Class<U> clazz, String id) {
		return (U)dicoIdToObject.get(id);
	}

	public <U> void addObject(U obj, String id) {
		dicoIdToObject.put(id, obj);
	}
}
