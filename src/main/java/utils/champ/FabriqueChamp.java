package utils.champ;

import java.lang.reflect.Field;

import utils.TypeExtension;

public interface FabriqueChamp {
	public static Champ createChamp(final Field info) {
		return new Champ(info, TypeExtension.isSimple(info.getType()), info.getName().equals(ChampUid.UID_FIELD_NAME));
	}

	public static ChampUid createChampId(final Class<?> typeObject) {
		return new ChampUid(typeObject);
	}

}
