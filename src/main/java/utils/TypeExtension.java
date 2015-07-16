package utils;


import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.ArrayUtils;

import utils.champ.Champ;
import utils.champ.ChampUid;
import utils.champ.FabriqueChamp;

import com.actemium.marshall.annotations.IgnoreSerialise;
import com.actemium.marshall.annotations.TypeRelation;

public class TypeExtension {
	
	private static Set<Class<?>> simpleTypes = new HashSet<Class<?>>(Arrays.asList(Boolean.class, Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class, 
			String.class, Date.class, void.class, UUID.class)); 

	public static boolean isSimple(Class<?> type) { // Simple types become XML Attributes and JSON Values
		return type.isPrimitive() || type.isEnum() || simpleTypes.contains(type);
	}

	private static Map<Class<?>, List<Champ>> serializablefieldsOfType = new HashMap<Class<?>, List<Champ>>();
	
	private static Map<Class<?>, Champ> dicoTypeTochampId = new HashMap<>();

	@SuppressWarnings("unchecked")
	public static <ObjetParent, TypeChamp, ObjetComposite extends TypeChamp> Champ getChampComposite(ObjetParent objetParent, ObjetComposite objetComposite){
		Class<ObjetParent> typeObjetParent = (Class<ObjetParent>) objetParent.getClass();
		List<Champ> champs = getSerializableFields(typeObjetParent);
		for(Champ champ : champs){
			try {
				if (champ.relation == TypeRelation.COMPOSITION && champ.info.get(objetParent) == objetComposite)
					return champ;
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static  Champ getChamp(Object objetParent, Object objetEnfant){
		Class<?> typeObjetParent = objetParent.getClass();
		List<Champ> champs = getSerializableFields(typeObjetParent);
		for(Champ champ : champs){
			try {
				if (champ.info.get(objetParent) == objetEnfant)
					return champ;
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static  Champ getChampByName(Class<?> typeObjetParent, String name){
		List<Champ> champs = getSerializableFields(typeObjetParent);
		for(Champ champ : champs){
			if (champ.name.equals(name))
				return champ;
		}
		return null;
	}
	
	public static List<Champ> getCompositionField(Class<?> typeObjetParent){
		List<Champ> lRet = new ArrayList<>();
		List<Champ> champs = getSerializableFields(typeObjetParent);
		for(Champ champ : champs){
			if(champ.relation == TypeRelation.COMPOSITION)
				lRet.add(champ);
		}
		return lRet;
	}
	
	public static List<Champ> getSerializableFields(Class<?> typeObj) {
		List<Champ> fields = serializablefieldsOfType.get(typeObj);
		if (fields == null){
			fields = new LinkedList<Champ>();
			Boolean hasUid = false;
			Class<?> parent = typeObj;
			Field[] fieldstmp = new Field[0];
			while(parent != Object.class){
				fieldstmp = ArrayUtils.addAll(fieldstmp, parent.getDeclaredFields());
				parent = parent.getSuperclass();
			}
			for (Field info : fieldstmp) {
				info.setAccessible(true);
				if (info.getAnnotation(IgnoreSerialise.class)==null && !Modifier.isFinal(info.getModifiers())) {
					//on ne sérialise pas les attributs finaux ni ceux a ne pas sérialiser
					Champ champ = FabriqueChamp.createChamp(info);
					fields.add(champ);
					if (champ.name == ChampUid.uidFieldName) {
						hasUid = true;
					}
				}
			}			
			if (!hasUid) {
				fields.add(FabriqueChamp.createChampId());
			}
			//tmp.sort();
			Collections.sort(fields);
			serializablefieldsOfType.put(typeObj, fields) ;
		}
		return fields;
	}
	
	public static Champ getChampId(Class<?> typeObjetParent){
		Champ champId = dicoTypeTochampId.get(typeObjetParent);
		if(champId == null){
			for (Champ champ : TypeExtension.getSerializableFields(typeObjetParent)){
				if (champ.name == ChampUid.uidFieldName) {
					 champId = champ;
					 break;
				}
			}
			dicoTypeTochampId.put(typeObjetParent, champId);
		}
		return champId;
	}

	@SuppressWarnings("unchecked")
	public static <U> boolean isNotNullAndSizeNotNull(U obj) {
		if(obj == null)
			return false;
		Class<U> type = (Class<U>) obj.getClass();
		boolean bRet = true;
		if (type != Constants.stringType && Constants.collectionType.isAssignableFrom(type)){
			bRet = ((Collection<?>)obj).size() > 0 ? true : false;
		}
		return bRet;
	}
	
	
	private static Map<Class<?>, Class<?>> dicoTypePrimitifToEnveloppe = new HashMap<>();
	static {
		dicoTypePrimitifToEnveloppe.put(boolean.class, Boolean.class);
		dicoTypePrimitifToEnveloppe.put(char.class, Character.class);
		dicoTypePrimitifToEnveloppe.put(byte.class, Byte.class);
		dicoTypePrimitifToEnveloppe.put(short.class, Short.class);
		dicoTypePrimitifToEnveloppe.put(int.class, Integer.class);
		dicoTypePrimitifToEnveloppe.put(long.class, Long.class);
		dicoTypePrimitifToEnveloppe.put(float.class, Float.class);
		dicoTypePrimitifToEnveloppe.put(double.class, Double.class);
	}
	public static Class<?> getTypeEnveloppe(Class<?> typePrimitif){
		if (!typePrimitif.isPrimitive()) return typePrimitif;
		return dicoTypePrimitifToEnveloppe.get(typePrimitif);
	}
}
