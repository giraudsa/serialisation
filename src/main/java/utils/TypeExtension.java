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

import giraudsa.marshall.exception.ChampNotFound;
import utils.champ.Champ;
import utils.champ.ChampUid;
import utils.champ.FabriqueChamp;
import utils.champ.FieldInformations;
import utils.champ.NullChamp;

public class TypeExtension {	
	private static final Set<Class<?>> simpleTypes = new HashSet<>(Arrays.asList(Boolean.class, Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class, 
			String.class, Date.class, void.class, UUID.class, Character.class, Void.class)); 
	private static final Map<Class<?>, Map<String, Champ>> serializablefieldsOfType = new HashMap<>();
	private static final Map<Class<?>, List<Champ>> fieldsOfType = new HashMap<>();
	private static final Map<Class<?>, Champ> dicoTypeTochampId = new HashMap<>();
	private static final Map<Class<?>, Class<?>> dicoTypePrimitifToEnveloppe = new HashMap<>();
	private static final Set<Class<?>> simpleEnveloppe = new HashSet<>();
	
	private TypeExtension(){
		//privateconstructor to hide explicit public one
	}

	public static boolean isSimple(Class<?> type) { // Simple types become XML Attributes and JSON Values
		return type.isPrimitive() || type.isEnum() || simpleTypes.contains(type);
	}

	
	public static  FieldInformations getChampByName(Class<?> typeObjetParent, String name){
		getSerializableFields(typeObjetParent);
		if(serializablefieldsOfType.get(typeObjetParent).containsKey(name))
			return serializablefieldsOfType.get(typeObjetParent).get(name);
		if(ConfigurationMarshalling.isModelContraignant())
			throw new ChampNotFound("le champ " + name + " n'existe pas dans l'objet de type " + typeObjetParent.getName());
		return NullChamp.getInstance();
	}
	
	
	public static synchronized List<Champ> getSerializableFields(Class<?> typeObj) {
		List<Champ> fields = fieldsOfType.get(typeObj);
		if (fields == null){
			fields = new ArrayList<>();
			Map<String, Champ> mapFields = new HashMap<>();
			serializablefieldsOfType.put(typeObj, mapFields);
			Boolean hasUid = false;
			Class<?> parent = typeObj;
			List<Field> fieldstmp = new ArrayList<>();
			while(parent != Object.class){
				Collections.addAll(fieldstmp, parent.getDeclaredFields());
				parent = parent.getSuperclass();
			}
			for (Field info : fieldstmp) {
				info.setAccessible(true);
				if (!isTransient(info) && !(Modifier.isFinal(info.getModifiers()) && Modifier.isStatic(info.getModifiers())) && info.getType().getName().indexOf("Logger") == -1) {
					//on ne sérialise pas les attributs static finaux ni ceux a ne pas sérialiser ni les attributs techniques de log.
					Champ champ = FabriqueChamp.createChamp(info);
					mapFields.put(champ.getName(), champ);
					fields.add(champ);
					hasUid = hasUid || champ.getName().equals(ChampUid.UID_FIELD_NAME);
				}
			}			
			if (!hasUid) {
				mapFields.put(ChampUid.UID_FIELD_NAME, FabriqueChamp.createChampId(typeObj));
			}
			Collections.sort(fields);
			fieldsOfType.put(typeObj, fields) ;
		}
		return fields;
	}

	private static boolean isTransient(Field info) {
        return info.getAnnotation(ConfigurationMarshalling.getAnnotationIgnoreSerialise()) != null;
   }

	
	public static synchronized Champ getChampId(Class<?> typeObjetParent){
		Champ champId = dicoTypeTochampId.get(typeObjetParent);
		if(champId == null){
			getSerializableFields(typeObjetParent);
			champId = serializablefieldsOfType.get(typeObjetParent).get(ChampUid.UID_FIELD_NAME);
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

	static synchronized void clear() {
		serializablefieldsOfType.clear();
		fieldsOfType.clear();
	}
}
