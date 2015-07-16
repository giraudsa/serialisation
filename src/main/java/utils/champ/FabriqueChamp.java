package utils.champ;

import java.lang.reflect.Field;

import utils.TypeExtension;

public abstract class FabriqueChamp {
	public static Champ createChamp(Field info) {
		return new Champ(info, TypeExtension.isSimple(info.getType()));
	}
	
	public static <Obj> ChampUid createChampId(){
		return new ChampUid();
	}
}
