package io.github.giraudsa.fidelis.utils;

import java.lang.reflect.Array;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import static java.util.Map.entry;

import io.github.giraudsa.fidelis.exception.UnmarshallExeption;
import io.github.giraudsa.fidelis.strategie.StrategieDeSerialisation;
import io.github.giraudsa.fidelis.strategie.StrategieParComposition;
import io.github.giraudsa.fidelis.strategie.StrategieParCompositionOuAgregationEtClasseConcrete;
import io.github.giraudsa.fidelis.strategie.StrategieSerialisationComplete;

public class Constants {

	public static final Class<?> arrayType = Array.class;
	private static final String BOOLEAN_TYPE = "bool";

	private static final String BYTE_TYPE = "byte";
	public static final Class<?> calendarType = Calendar.class;

	@SuppressWarnings("rawtypes")
	private static final Class[] classVide = new Class[0];
	public static final String CLEF_TYPE = "__type";
	public static final String CLEF_TYPE_ID_UNIVERSEL = "@type";
	private static final String COLLECTION_TYPE = "list";
	public static final Class<?> collectionType = Collection.class;
	private static final String DATE_TYPE = "date";
	public static final Class<?> dateType = Date.class;
	public static final Class<?> dictionaryType = Map.class;
	private static final String DICTIONNAIRE_TYPE = "map";
	private static final String DOUBLE_TYPE = "double";
	public static final Class<?> enumType = Enum.class;
	private static final String FLOAT_TYPE = "float";
	public static final Class<?> inetAdress = InetAddress.class;
	private static final String INTEGER_TYPE = "int";
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

    private static final Map<String, String> dicoSimpleNameToName = Map.ofEntries(
        entry(DICTIONNAIRE_TYPE, HashMap.class.getName()),
        entry(COLLECTION_TYPE, ArrayList.class.getName()),
        entry(INTEGER_TYPE, Integer.class.getName()),
        entry(DOUBLE_TYPE, Double.class.getName()),
        entry(FLOAT_TYPE, Float.class.getName()),
        entry(BOOLEAN_TYPE, Boolean.class.getName()),
        entry(SHORT_TYPE, Short.class.getName()),
        entry(BYTE_TYPE, Byte.class.getName()),
        entry(LONG_TYPE, Long.class.getName()),
        entry(UUID_TYPE, UUID.class.getName()),
        entry(STRING_TYPE, String.class.getName()),
        entry(DATE_TYPE, Date.class.getName()),
        entry(VOID_TYPE, Void.class.getName())
    );

    private static final Map<Class<?>, String> dicoClassToSimpleName = Map.ofEntries(
        entry(HashMap.class, DICTIONNAIRE_TYPE),
        entry(ArrayList.class, COLLECTION_TYPE),
        entry(Integer.class, INTEGER_TYPE),
        entry(Double.class, DOUBLE_TYPE),
        entry(Float.class, FLOAT_TYPE),
        entry(Boolean.class, BOOLEAN_TYPE),
        entry(Short.class, SHORT_TYPE),
        entry(Byte.class, BYTE_TYPE),
        entry(Long.class, LONG_TYPE),
        entry(UUID.class, UUID_TYPE),
        entry(String.class, STRING_TYPE),
        entry(Date.class, DATE_TYPE),
        entry(Void.class, VOID_TYPE)
    );

	/** stratégies connues, sans état, indexées par leur octet de tête dans le flux binaire. */
	private static final StrategieDeSerialisation[] STRATEGIES_CONNUES = { new StrategieSerialisationComplete(),
			new StrategieParComposition(), new StrategieParCompositionOuAgregationEtClasseConcrete() };


	@SuppressWarnings("rawtypes")
	public static final Class[] getClassVide() {
		return classVide;
	}

	public static byte getFirstByte(final StrategieDeSerialisation strategie) {
		// stratégies connues : comparaison directe (appelé à chaque sérialisation binaire)
		final Class<?> classe = strategie.getClass();
		for (byte b = 0; b < STRATEGIES_CONNUES.length; b++)
			if (STRATEGIES_CONNUES[b].getClass() == classe)
				return b;
		return STRATEGIE_INCONNUE;
	}

	public static String getNameType(final String smallName) {
		var typeName = dicoSimpleNameToName.get(smallName);
		if (typeName == null)
			typeName = smallName;
		return typeName;
	}

	public static final Object[] getNullArgument() {
		return nullArgument;
	}

	public static String getSmallNameType(final Class<?> clazz) {
		var smallName = dicoClassToSimpleName.get(clazz);
		if (smallName == null)
			smallName = clazz.getName();
		return smallName;
	}

	public static StrategieDeSerialisation getStrategie(final byte firstByte) throws UnmarshallExeption {
		if (firstByte == STRATEGIE_INCONNUE)
			return null;
		// les stratégies connues sont sans état : une instance partagée (appelé à chaque désérialisation binaire)
		if (firstByte >= 0 && firstByte < STRATEGIES_CONNUES.length)
			return STRATEGIES_CONNUES[firstByte];
		throw new UnmarshallExeption("stratégie de désérialisation inconnue : " + firstByte);
	}

	private Constants() {
		// private constructueur to hide implicit public one
	}
}
