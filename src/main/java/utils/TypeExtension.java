package utils;


import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import utils.champ.Champ;
import utils.champ.ChampUid;
import utils.champ.FabriqueChamp;

public class TypeExtension {	
	private static Set<Class<?>> simpleTypes = new HashSet<Class<?>>(Arrays.asList(Boolean.class, Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class, 
			String.class, Date.class, void.class, UUID.class, Character.class, Void.class)); 
	protected static Map<Class<?>, List<Champ>> serializablefieldsOfType = new HashMap<Class<?>, List<Champ>>();
	private static Map<Class<?>, Champ> dicoTypeTochampId = new HashMap<Class<?>, Champ>();
	private static Map<Class<?>, Class<?>> dicoTypePrimitifToEnveloppe = new HashMap<Class<?>, Class<?>>();
	private static final Set<Class<?>> simpleEnveloppe = new HashSet<Class<?>>();
	
	private TypeExtension(){
		//privateconstructor to hide explicit public one
	}

	public static boolean isSimple(Class<?> type) { // Simple types become XML Attributes and JSON Values
		return type.isPrimitive() || type.isEnum() || simpleTypes.contains(type);
	}

	
	public static  Champ getChampByName(Class<?> typeObjetParent, String name){
		List<Champ> champs = getSerializableFields(typeObjetParent);
		for(Champ champ : champs){
			if (champ.getName().equals(name))
				return champ;
		}
		return null;
	}
	
	
	public static synchronized List<Champ> getSerializableFields(Class<?> typeObj) {
		List<Champ> fields = serializablefieldsOfType.get(typeObj);
		if (fields == null){
			fields = new ArrayList<Champ>();
			Boolean hasUid = false;
			Class<?> parent = typeObj;
			List<Field> fieldstmp = new ArrayList<Field>();
			while(parent != Object.class){
				Collections.addAll(fieldstmp, parent.getDeclaredFields());
				parent = parent.getSuperclass();
			}
			for (Field info : fieldstmp) {
				info.setAccessible(true);
				if (!isTransient(info) && !Modifier.isFinal(info.getModifiers()) && info.getType().getName().indexOf("Logger") == -1) {
					//on ne sérialise pas les attributs finaux ni ceux a ne pas sérialiser ni les attributs techniques de log.
					Champ champ = FabriqueChamp.createChamp(info);
					fields.add(champ);
					hasUid = hasUid || champ.getName().equals(ChampUid.UID_FIELD_NAME);
				}
			}			
			if (!hasUid) {
				fields.add(FabriqueChamp.createChampId(typeObj));
			}
			Collections.sort(fields);
			serializablefieldsOfType.put(typeObj, fields) ;
		}
		return fields;
	}

	private static boolean isTransient(Field info) {
        return info.getAnnotation(ConfigurationMarshalling.getAnnotationIgnoreSerialise()) != null;
   }

	
	public static synchronized Champ getChampId(Class<?> typeObjetParent){
		Champ champId = dicoTypeTochampId.get(typeObjetParent);
		if(champId == null){
			for (Champ champ : TypeExtension.getSerializableFields(typeObjetParent)){
				if (champ.getName() == ChampUid.UID_FIELD_NAME) {
					 champId = champ;
					 break;
				}
			}
			dicoTypeTochampId.put(typeObjetParent, champId);
		}
		return champId;
	}
	
	static {
		dicoTypePrimitifToEnveloppe.put(void.class, Void.class);
		dicoTypePrimitifToEnveloppe.put(boolean.class, Boolean.class);
		dicoTypePrimitifToEnveloppe.put(char.class, Character.class);
		dicoTypePrimitifToEnveloppe.put(byte.class, Byte.class);
		dicoTypePrimitifToEnveloppe.put(short.class, Short.class);
		dicoTypePrimitifToEnveloppe.put(int.class, Integer.class);
		dicoTypePrimitifToEnveloppe.put(long.class, Long.class);
		dicoTypePrimitifToEnveloppe.put(float.class, Float.class);
		dicoTypePrimitifToEnveloppe.put(double.class, Double.class);
		simpleEnveloppe.add(Boolean.class);
		simpleEnveloppe.add(Byte.class);
		simpleEnveloppe.add(Character.class);
		simpleEnveloppe.add(Short.class);
		simpleEnveloppe.add(Integer.class);
		simpleEnveloppe.add(Long.class);
		simpleEnveloppe.add(Double.class);
		simpleEnveloppe.add(Float.class);
		simpleEnveloppe.add(void.class);
		simpleEnveloppe.add(Void.class);
	}
	public static Class<?> getTypeEnveloppe(Class<?> typePrimitif){
		if (typePrimitif == null || !typePrimitif.isPrimitive())
			return typePrimitif;
		return dicoTypePrimitifToEnveloppe.get(typePrimitif);
	}
	public static boolean isSimpleBinary(Class<?> clazz){
		return clazz.isPrimitive() || simpleEnveloppe.contains(clazz) || clazz.isEnum();
	}
	public static boolean isEnveloppe(Class<?> clazz){
		return simpleEnveloppe.contains(clazz);
	}
}
