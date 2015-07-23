package utils;

import giraudsa.marshall.deserialisation.text.json.Container;
import giraudsa.marshall.serialisation.text.json.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Constants {
	public static final byte IS_NULL = 					(byte) 0x00;//0b 0000 0000
	public static final byte IS_FINI = 					(byte) 0xff;//0b 1111 1111
	
	public static final class SMALL_ID_TYPE{
		public static final byte NEXT_IS_SMALL_ID_BYTE =	(byte) 0x01;//0b 0000 0001
		public static final byte NEXT_IS_SMALL_ID_SHORT =	(byte) 0x02;//0b 0000 0010
		public static final byte NEXT_IS_SMALL_ID_INT =		(byte) 0x03;//0b 0000 0011
		private static final byte MASK_SID_TYPE = 			(byte) 0x03;//0b 0000 0011
		
		public static byte getSmallId(byte candidat){
			return (byte) (candidat & MASK_SID_TYPE);
		}
		
	}
	
	public static final class BOOL_VALUE{
		private static final byte B_MASQUE = 		(byte) 0x0c;//0b 0000 1100
		private static final byte B_NULL =			(byte) 0x04;//0b 0000 0100	
		private static final byte TRUE = 			(byte) 0x08;//0b 0000 1000	
		private static final byte FALSE = 			(byte) 0x0c;//0b 0000 1100
		
		private static final Map<Boolean, Byte> dicoBoolToByte = new HashMap<>();
		private static final Map<Byte, Boolean> dicoByteToBool = new HashMap<>();
		static {
			dicoBoolToByte.put(null, B_NULL);
			dicoBoolToByte.put(true, TRUE);
			dicoBoolToByte.put(false, FALSE);
			dicoByteToBool.put(B_NULL, null);
			dicoByteToBool.put(TRUE, true);
			dicoByteToBool.put(FALSE, false);
		}
		
		public static byte getByte(Boolean b){
			return dicoBoolToByte.get(b);
		}
		public static Boolean getBool(byte b){
			byte t = (byte) (b & B_MASQUE);
			return dicoByteToBool.get(t);
		}
	}

	
	public final static class Type{
		private static final byte MASQUE = 		(byte) 0xf0;
		public static final byte CODAGE_INT = 	(byte) 0x10;
		public static final byte CODAGE_BYTE =	(byte) 0x20;
		public static final byte CODAGE_SHORT =	(byte) 0x30;
		public static final byte BOOL =			(byte) 0x40;
		public static final byte BYTE =	 		(byte) 0x50;
		public static final byte SHORT =	 	(byte) 0x60;
		public static final byte INT = 			(byte) 0x70;
		public static final byte LONG = 		(byte) 0x80;
		public static final byte FLOAT = 		(byte) 0x90;
		public static final byte DOUBLE = 		(byte) 0xa0;
		public static final byte UUID = 		(byte) 0xb0;
		public static final byte STRING = 		(byte) 0xc0;
		public static final byte DATE = 		(byte) 0xd0;
		public static final byte CHAR = 		(byte) 0xe0;
		public static final byte AUTRE = 		(byte) 0x00;
		
		public static byte getLongueurCodageType(byte header){
			return (byte) (header & MASQUE);
		}
		private static final Map<Byte, Class<?>> dicoByteToTypeSimple = new HashMap<>();
		private static final Map<Class<?>, Byte> dicoTypeSimpleToByte = new HashMap<>();
		
		static {
			dicoByteToTypeSimple.put(BOOL, Boolean.class);
			dicoByteToTypeSimple.put(BYTE, Byte.class);
			dicoByteToTypeSimple.put(SHORT, Short.class);
			dicoByteToTypeSimple.put(INT, Integer.class);
			dicoByteToTypeSimple.put(LONG, Long.class);
			dicoByteToTypeSimple.put(FLOAT, Float.class);
			dicoByteToTypeSimple.put(DOUBLE, Double.class);
			dicoByteToTypeSimple.put(UUID, UUID.class);
			dicoByteToTypeSimple.put(STRING, String.class);
			dicoByteToTypeSimple.put(DATE, Date.class);
			dicoByteToTypeSimple.put(CHAR, Character.class);
			dicoTypeSimpleToByte.put(Boolean.class, BOOL);
			dicoTypeSimpleToByte.put(Byte.class, BYTE);
			dicoTypeSimpleToByte.put(Short.class, SHORT);
			dicoTypeSimpleToByte.put(Integer.class, INT);
			dicoTypeSimpleToByte.put(Long.class, LONG);
			dicoTypeSimpleToByte.put(Float.class, FLOAT);
			dicoTypeSimpleToByte.put(Double.class, DOUBLE);
			dicoTypeSimpleToByte.put(UUID.class, UUID);
			dicoTypeSimpleToByte.put(String.class, STRING);
			dicoTypeSimpleToByte.put(Date.class, DATE);
			dicoTypeSimpleToByte.put(Character.class, CHAR);
		}
		
		public static byte getByteHeader(Class<?> typePrimitif){
			Class<?> typeSimple = TypeExtension.getTypeEnveloppe(typePrimitif);
			Byte ret = dicoTypeSimpleToByte.get(typeSimple);
			if (ret == null) ret = AUTRE;
			return ret;
		}
		
		public static Class<?> getSimpleType(byte b, Class<?> typeProbable){
			byte t = (byte) (b & MASQUE);
			Class<?> ret = dicoByteToTypeSimple.get(t);
			if(ret == null) ret = typeProbable;
			return ret;
		}
		
	}
	
	
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
