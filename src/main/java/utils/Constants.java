package utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.AbstractMap.SimpleEntry;

import com.actemium.marshall.deserialisation.json.Container;
import com.actemium.marshall.serialisation.json.Pair;

public abstract class Constants {
	
	public static final String MAP_CLEF = "__map__clef";
	public static final String MAP_VALEUR = "__map__valeur";
	public static final String MAP_TYPE = "__entry__set";
	public static final String CLEF_TYPE = "__type";
	
	public static Class<?> dictionaryType = Map.class;
	public static Class<?> collectionType = Collection.class;
	public static Class<?> objectType = Object.class;
	public static Class<?> enumType = Enum.class;
	public static Class<?> stringType = String.class;
	public static Class<?> dateType = Date.class;
	
	private static String DICTIONNAIRE_TYPE = "map";
	private static String COLLECTION_TYPE = "list";
	private static String INTEGER_TYPE = "int";
	private static String DOUBLE_TYPE = "double";
	private static String FLOAT_TYPE = "float";
	private static String BOOLEAN_TYPE = "bool";
	private static String SHORT_TYPE = "short";
	private static String BYTE_TYPE = "byte";
	private static String LONG_TYPE = "long";
	private static String UUID_TYPE = "uuid";
	private static String STRING_TYPE = "string";
	private static String DATE_TYPE = "date";
	private static String VOID_TYPE = "void";
	private static String CONTAINER_TYPE = "__container";
	
	private static Map<String, String> dicoSimpleNameToName = new HashMap<>();
	private static Map<Class<?>, String> dicoClassToSimpleName = new HashMap<>();
	static{
		dicoSimpleNameToName.put(DICTIONNAIRE_TYPE, HashMap.class.getName());
		dicoSimpleNameToName.put(COLLECTION_TYPE, ArrayList.class.getName());
		dicoSimpleNameToName.put(INTEGER_TYPE, Integer.class.getName());
		dicoSimpleNameToName.put(DOUBLE_TYPE, Double.class.getName());
		dicoSimpleNameToName.put(FLOAT_TYPE, Float.class.getName());
		dicoSimpleNameToName.put(BOOLEAN_TYPE, Boolean.class.getName());
		dicoSimpleNameToName.put(SHORT_TYPE, Short.class.getName());
		dicoSimpleNameToName.put(BYTE_TYPE, Byte.class.getName());
		dicoSimpleNameToName.put(LONG_TYPE, Long.class.getName());
		dicoSimpleNameToName.put(UUID_TYPE, UUID.class.getName());
		dicoSimpleNameToName.put(STRING_TYPE, String.class.getName());
		dicoSimpleNameToName.put(DATE_TYPE, Date.class.getName());
		dicoSimpleNameToName.put(VOID_TYPE, Void.class.getName());
		dicoSimpleNameToName.put(CONTAINER_TYPE, Container.class.getName());
		dicoSimpleNameToName.put(MAP_TYPE, Pair.class.getName());
		dicoClassToSimpleName.put(HashMap.class, DICTIONNAIRE_TYPE);
		dicoClassToSimpleName.put(ArrayList.class, COLLECTION_TYPE);
		dicoClassToSimpleName.put(Integer.class, INTEGER_TYPE);
		dicoClassToSimpleName.put(Double.class, DOUBLE_TYPE);
		dicoClassToSimpleName.put(Float.class, FLOAT_TYPE);
		dicoClassToSimpleName.put(Boolean.class, BOOLEAN_TYPE);
		dicoClassToSimpleName.put(Short.class, SHORT_TYPE);
		dicoClassToSimpleName.put(Byte.class, BYTE_TYPE);
		dicoClassToSimpleName.put(Long.class, LONG_TYPE);
		dicoClassToSimpleName.put(UUID.class, UUID_TYPE);
		dicoClassToSimpleName.put(String.class, STRING_TYPE);
		dicoClassToSimpleName.put(Date.class, DATE_TYPE);
		dicoClassToSimpleName.put(Void.class, VOID_TYPE);
		dicoClassToSimpleName.put(Container.class, CONTAINER_TYPE);
		dicoClassToSimpleName.put(Pair.class, MAP_TYPE);
	}
	
	public static String getSmallNameType(Class<?> clazz){
		String smallName = dicoClassToSimpleName.get(clazz);
		if (smallName == null) smallName = clazz.getName();
		return smallName;
	}
	
	public static String getNameType(String smallName){
		String typeName = dicoSimpleNameToName.get(smallName);
		if (typeName == null) typeName = smallName;
		return typeName;
	}
}
