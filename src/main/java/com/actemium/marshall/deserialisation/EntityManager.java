package com.actemium.marshall.deserialisation;

public interface EntityManager {

	public <U> U findObject(String id, Class<U> type);
	
}
