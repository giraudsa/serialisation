package utils.champ;

import java.util.UUID;

public class ChampUid extends Champ {

	public static String uidFieldName = "id";
	public UUID uuid;
	
	ChampUid() {
		super(null, true);
		name = uidFieldName;
		uuid = UUID.randomUUID();
	}
	
	@Override
	public  void set(Object obj, Object value) {
		uuid = (UUID) value;
	}

	@Override
	public UUID get(Object obj) {
		return uuid;
	}

	@Override
	public int compareTo(Champ other) {
		return other instanceof ChampUid ? 0 : -1;
	}
}
