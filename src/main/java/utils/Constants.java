package utils;

import java.lang.reflect.Array;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import giraudsa.marshall.exception.UnmarshallExeption;
import giraudsa.marshall.strategie.StrategieDeSerialisation;
import giraudsa.marshall.strategie.StrategieParComposition;
import giraudsa.marshall.strategie.StrategieParCompositionOuAgregationEtClasseConcrete;
import giraudsa.marshall.strategie.StrategieSerialisationComplete;

public class Constants {

	public static final Class<?> arrayType = Array.class;
	private static final String BOOLEAN_TYPE = "bool";

	private static final String BYTE_TYPE = "byte";
	private static MapDoubleSens<Byte, Class<? extends StrategieDeSerialisation>> byteToStrategie = new MapDoubleSens<>();
	public static final Class<?> calendarType = Calendar.class;

	@SuppressWarnings("rawtypes")
	private static final Class[] classVide = new Class[0];
	public static final String CLEF_TYPE = "__type";
	public static final String CLEF_TYPE_ID_UNIVERSEL = "@type";
	private static final String COLLECTION_TYPE = "list";
	public static final Class<?> collectionType = Collection.class;
	private static final String DATE_TYPE = "date";
	public static final Class<?> dateType = Date.class;
	private static final Map<Class<?>, String> dicoClassToSimpleName = new HashMap<>();
	private static final Map<String, String> dicoSimpleNameToName = new HashMap<>();

	public static final Class<?> dictionaryType = Map.class;
	private static final String DICTIONNAIRE_TYPE = "map";
	private static final String DOUBLE_TYPE = "double";
	public static final Class<?> enumType = Enum.class;
	private static final String FLOAT_TYPE = "float";
	public static final Class<?> inetAdress = InetAddress.class;
	private static final String INTEGER_TYPE = "int";
	public static final byte IS_NULL = (byte) 0x00;// 0b 0000 0000
	private static final String LONG_TYPE = "long";
	public static final String MAP_CLEF = "__map__clef";
	public static final String MAP_TYPE = "__entry__set";
	public static final String MAP_VALEUR = "__map__valeur";
	private static final Object[] nullArgument = new Object[0];
	public static final Class<?> objectType = Object.class;
	public static final byte SERIALISATION_COMPLETE = (byte) 0x00;
	private static final String SHORT_TYPE = "short";
	public static final byte STRATEGIE_INCONNUE = (byte) 254;
	private static final String STRING_TYPE = "string";
	public static final Class<?> stringType = String.class;

	private static final String UUID_TYPE = "uuid";
	public static final String VALEUR = "__valeur";
	private static final String VOID_TYPE = "void";

	static {
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

		byteToStrategie.put(SERIALISATION_COMPLETE, StrategieSerialisationComplete.class);
		byteToStrategie.put((byte) 1, StrategieParComposition.class);
		byteToStrategie.put((byte) 2, StrategieParCompositionOuAgregationEtClasseConcrete.class);
	}

	@SuppressWarnings("rawtypes")
	public static final Class[] getClassVide() {
		return classVide;
	}

	public static byte getFirstByte(final StrategieDeSerialisation strategie) {
		if (!byteToStrategie.containsValue(strategie.getClass()))
			return STRATEGIE_INCONNUE;
		else
			return byteToStrategie.getReverse(strategie.getClass());
	}

	public static String getNameType(final String smallName) {
		String typeName = dicoSimpleNameToName.get(smallName);
		if (typeName == null)
			typeName = smallName;
		return typeName;
	}

	public static final Object[] getNullArgument() {
		return nullArgument;
	}

	public static String getSmallNameType(final Class<?> clazz) {
		String smallName = dicoClassToSimpleName.get(clazz);
		if (smallName == null)
			smallName = clazz.getName();
		return smallName;
	}

	public static StrategieDeSerialisation getStrategie(final byte firstByte) throws UnmarshallExeption {
		if (firstByte == STRATEGIE_INCONNUE)
			return null;
		try {
			return byteToStrategie.get(firstByte).newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new UnmarshallExeption("impossible d'instancier la strategie de deserialisation", e);
		}
	}

	private Constants() {
		// private constructueur to hide implicit public one
	}
}
