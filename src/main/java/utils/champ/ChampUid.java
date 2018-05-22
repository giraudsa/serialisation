package utils.champ;

import java.util.Map;
import java.util.UUID;

import utils.EntityManager;

public class ChampUid extends Champ {

	public static final String UID_FIELD_NAME = "id";

	private final Class<?> typeObject;

	ChampUid(final Class<?> typeObject) {
		super(null, true, true);
		this.typeObject = typeObject;
		name = UID_FIELD_NAME;
		valueType = String.class;
	}

	@Override
	public int compareTo(final Champ other) {
		return other instanceof ChampUid ? 0 : -1;
	}

	@Override
	public boolean equals(final Object other) {
		if (other instanceof ChampUid)
			return other == this;
		return false;
	}

	@Override
	public synchronized String get(final Object obj, final Map<Object, UUID> dicoObjToFakeId,
			final EntityManager entity) {
		if (entity != null && entity.getId(obj) != null)
			return entity.getId(obj);
		if (dicoObjToFakeId == null)
			return UUID.randomUUID().toString();
		if (!dicoObjToFakeId.containsKey(obj))
			dicoObjToFakeId.put(obj, UUID.randomUUID());
		return dicoObjToFakeId.get(obj).toString();
	}

	@Override
	public int hashCode() {
		return typeObject.hashCode();
	}

	@Override
	public boolean isChampId() {
		return true;
	}

	@Override
	public void set(final Object obj, final Object value, final Map<Object, UUID> dicoObjToFakeId) {
		dicoObjToFakeId.put(obj, UUID.fromString(value.toString()));
	}
}
