package utils;

import giraudsa.marshall.exception.InstanciationException;

public interface EntityManager {

	public <U> U findObjectOrCreate(String id, Class<U> clazz) throws InstanciationException;
	public String getId(Object objet);
	
}
