package utils.champ;

import java.lang.reflect.Field;

import utils.TypeExtension;

public abstract class FabriqueChamp {
	private FabriqueChamp(){
		//private constructor to hide implicit public one
	}
	public static Champ createChamp(Field info) {
		return new Champ(info, TypeExtension.isSimple(info.getType()), info.getName().equals(ChampUid.UID_FIELD_NAME));
	}
	
	public static ChampUid createChampId(Class<?> typeObject){
		return new ChampUid(typeObject);
	}
}
