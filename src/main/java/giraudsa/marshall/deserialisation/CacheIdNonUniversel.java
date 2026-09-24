package giraudsa.marshall.deserialisation;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Cache des objets par (id, classe exacte). Une map d'id par classe, sans clé composée allouée à chaque accès ; la
 * dernière classe consultée est gardée (les accès se suivent souvent pour une même classe).
 */
public class CacheIdNonUniversel implements CacheObject {

	private final Map<Class<?>, Map<String, Object>> dicoClassToIdToObject = new IdentityHashMap<>();
	private Class<?> derniereClasse;
	private Map<String, Object> derniereTable;

	private Map<String, Object> table(final Class<?> clazz, final boolean creer) {
		if (clazz == derniereClasse)
			return derniereTable;
		Map<String, Object> table = dicoClassToIdToObject.get(clazz);
		if (table == null) {
			if (!creer)
				return null;
			table = new HashMap<>();
			dicoClassToIdToObject.put(clazz, table);
		}
		derniereClasse = clazz;
		derniereTable = table;
		return table;
	}

	@Override
	public <U> void addObject(final U object, final String id) {
		table(object.getClass(), true).put(id, object);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <U> U getObject(final Class<U> clazz, final String id) {
		final Map<String, Object> table = table(clazz, false);
		return table == null ? null : (U) table.get(id);
	}

}
