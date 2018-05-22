package giraudsa.marshall.deserialisation;

public class CacheEmpty implements CacheObject {

	@Override
	public <U> void addObject(final U object, final String id) {
		// rien a ajouter dans un cache vide
	}

	@Override
	public <U> U getObject(final Class<U> clazz, final String id) {
		return null;
	}

}
