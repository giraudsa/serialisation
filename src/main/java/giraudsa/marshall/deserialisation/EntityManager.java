package giraudsa.marshall.deserialisation;

import giraudsa.marshall.exception.InstanciationException;

public interface EntityManager {

	public <U> U findObjectOrCreate(String id, Class<U> clazz, boolean fromExt) throws InstanciationException;
	
}
