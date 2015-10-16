package giraudsa.marshall.deserialisation;

public interface EntityManager {

	public <U> U findObject(String id, Class<U> type);
	public <U> void metEnCache(String id, U obj);
	
}
