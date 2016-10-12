package utils.champ;

import java.util.Map;
import java.util.UUID;

public class ChampUid extends Champ {

	public static final String UID_FIELD_NAME = "id";
	
	
	private Class<?> typeObject;
	
	ChampUid(Class<?> typeObject) {
		super(null, true, true);
		this.typeObject = typeObject;
		name = UID_FIELD_NAME;
		valueType = UUID.class;
	}
	
	@Override
	public  void set(Object obj, Object value, Map<Object, UUID> dicoObjToFakeId) {
		dicoObjToFakeId.put(obj, (UUID)value);
	}

	public synchronized UUID get(Object obj, Map<Object, UUID> dicoObjToFakeId) {
		if(dicoObjToFakeId == null)
			return UUID.randomUUID();
		if(!dicoObjToFakeId.containsKey(obj))
			dicoObjToFakeId.put(obj, UUID.randomUUID());
		return dicoObjToFakeId.get(obj);
	}

	@Override
	public int compareTo(Champ other) {
		return other instanceof ChampUid ? 0 : -1;
	}
	
	@Override
	public int hashCode() {
		return typeObject.hashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof ChampUid)
			return other == this;
		return false;
	}
	@Override
	public boolean isChampId() {
		return true;
	}
}
