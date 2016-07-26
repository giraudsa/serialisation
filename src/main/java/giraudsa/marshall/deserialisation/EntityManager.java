package giraudsa.marshall.deserialisation;

public interface EntityManager {

	public <U> U findObject(String id, Class<U> type);
	public <U> U findObjectOrCreate(String id, Class<U> clazz, boolean fromExt)
			throws InstantiationException, IllegalAccessException;
	
}
