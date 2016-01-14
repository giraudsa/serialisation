package giraudsa.marshall.deserialisation;

public class CacheEmpty implements CacheObject {

	@Override
	public <U> U getObject(Class<U> clazz, String id) {
		return null;
	}

	@Override
	public <U> void addObject(U object, String id) {
		//rien a ajouter dans un cache vide
	}

}
