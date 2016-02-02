package giraudsa.marshall.deserialisation;

public class CacheEmpty implements CacheObject {


	public <U> U getObject(Class<U> clazz, String id) {
		return null;
	}


	public <U> void addObject(U object, String id) {
		//rien a ajouter dans un cache vide
	}

}
