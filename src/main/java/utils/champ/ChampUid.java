package utils.champ;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChampUid extends Champ {

	public static String uidFieldName = "id";
	
	private static Map<Object, UUID> dicoObjToFakeId = new HashMap<>();
	
	ChampUid() {
		super(null, true);
		name = uidFieldName;
		valueType = UUID.class;
	}
	
	@Override
	public  void set(Object obj, Object value) {
		dicoObjToFakeId.put(obj, (UUID)value);
	}

	@Override
	public UUID get(Object obj) {
		if(!dicoObjToFakeId.containsKey(obj))
			dicoObjToFakeId.put(obj, UUID.randomUUID());
		return dicoObjToFakeId.get(obj);
	}

	@Override
	public int compareTo(Champ other) {
		return other instanceof ChampUid ? 0 : -1;
	}
}
