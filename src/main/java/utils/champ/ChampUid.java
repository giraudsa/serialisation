package utils.champ;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

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

	/**
	 * UUID version 4 tiré avec {@link ThreadLocalRandom} : l'identifiant doit
	 * seulement être unique dans le graphe, pas imprévisible. Évite le coût et la
	 * contention de SecureRandom utilisé par {@link UUID#randomUUID()}.
	 */
	static UUID nouveauFakeId() {
		final ThreadLocalRandom random = ThreadLocalRandom.current();
		long msb = random.nextLong();
		long lsb = random.nextLong();
		msb = msb & ~0xF000L | 0x4000L; // version 4
		lsb = lsb & 0x3FFFFFFFFFFFFFFFL | 0x8000000000000000L; // variante IETF
		return new UUID(msb, lsb);
	}

	@Override
	public String get(final Object obj, final Map<Object, UUID> dicoObjToFakeId, final EntityManager entity) {
		if (entity != null) {
			final String id = entity.getId(obj);
			if (id != null)
				return id;
		}
		if (dicoObjToFakeId == null)
			return nouveauFakeId().toString();
		return dicoObjToFakeId.computeIfAbsent(obj, o -> nouveauFakeId()).toString();
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
	public void affecte(final Object obj, final Object value, final Map<Object, UUID> dicoObjToFakeId) {
		set(obj, value, dicoObjToFakeId);
	}

	@Override
	public void set(final Object obj, final Object value, final Map<Object, UUID> dicoObjToFakeId) {
		dicoObjToFakeId.put(obj, UUID.fromString(value.toString()));
	}
}
