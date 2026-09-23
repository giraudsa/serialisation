package utils;

import static java.util.Map.entry;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import giraudsa.marshall.exception.ChampNotFound;
import utils.champ.Champ;
import utils.champ.ChampUid;
import utils.champ.FabriqueChamp;
import utils.champ.FieldInformations;
import utils.champ.NullChamp;

public class TypeExtension {
	/** Champs sérialisables d'une classe, calculés une seule fois. */
	private static final class ChampsDuType {
		private final Champ champId;
		private final List<Champ> champs;
		private final Map<String, Champ> champsParNom;
		private final List<Champ> champsSaufId;

		private ChampsDuType(final List<Champ> champs, final Map<String, Champ> champsParNom) {
			this.champs = Collections.unmodifiableList(champs);
			this.champsParNom = champsParNom;
			champId = champsParNom.get(ChampUid.UID_FIELD_NAME);
			final List<Champ> saufId = new ArrayList<>(champs);
			saufId.remove(champId);
			champsSaufId = Collections.unmodifiableList(saufId);
		}
	}

	private static final Map<Class<?>, ChampsDuType> champsParType = new ConcurrentHashMap<>();
	private static final Map<Class<?>, Class<?>> dicoTypePrimitifToEnveloppe = Map.ofEntries(
        entry(void.class, Void.class),
        entry(boolean.class, Boolean.class),
        entry(char.class, Character.class),
        entry(byte.class, Byte.class),
        entry(short.class, Short.class),
        entry(int.class, Integer.class),
        entry(long.class, Long.class),
        entry(float.class, Float.class),
        entry(double.class, Double.class)
    );
	/** getEnumConstants() renvoie une copie à chaque appel : on la garde par classe. */
	private static final ClassValue<Object[]> enumConstants = new ClassValue<>() {
		@Override
		protected Object[] computeValue(final Class<?> type) {
			// sous-classe anonyme d'une constante : les constantes sont sur la classe mère
			return (type.isEnum() ? type : type.getSuperclass()).getEnumConstants();
		}
	};
	private static final Map<Class<?>, Map<String, Enum<?>>> enumParNom = new ConcurrentHashMap<>();
	private static final ClassValue<Boolean> hibernate = new ClassValue<>() {
		@Override
		protected Boolean computeValue(final Class<?> type) {
			return type.getName().toLowerCase().indexOf("hibernate") != -1;
		}
	};
	private static final Set<Class<?>> simpleEnveloppe = Set.of(
        Boolean.class, Byte.class, Character.class, Short.class, Integer.class, Long.class,
        Double.class, Float.class, void.class, Void.class
    );
	private static final Set<Class<?>> simpleTypes = Set.of(Boolean.class, Byte.class, Short.class, Integer.class,
			Long.class, Float.class, Double.class, String.class, Date.class, void.class, UUID.class, Character.class,
			Void.class);

	private static ChampsDuType calculeChamps(final Class<?> typeObj) {
		final List<Champ> fields = new ArrayList<>();
		final Map<String, Champ> mapFields = new HashMap<>();
		var hasUid = false;
		var parent = typeObj;
		final var fieldstmp = new ArrayList<Field>();
		while (parent != Object.class) {
			Collections.addAll(fieldstmp, parent.getDeclaredFields());
			parent = parent.getSuperclass();
		}
		for (final var info : fieldstmp) {
			info.setAccessible(true);
			if (!isTransient(info)
					&& !(Modifier.isFinal(info.getModifiers()) && Modifier.isStatic(info.getModifiers()))
					&& info.getType().getName().indexOf("Logger") == -1) {
				// on ne sérialise pas les attributs static finaux ni ceux a ne pas sérialiser
				// ni les attributs techniques de log.
				final var champ = FabriqueChamp.createChamp(info);
				mapFields.put(champ.getName(), champ);
				fields.add(champ);
				hasUid = hasUid || champ.getName().equals(ChampUid.UID_FIELD_NAME);
			}
		}
		if (!hasUid) {
			final var champId = FabriqueChamp.createChampId(typeObj);
			fields.add(champId);
			mapFields.put(ChampUid.UID_FIELD_NAME, champId);
		}
		Collections.sort(fields);
		return new ChampsDuType(fields, mapFields);
	}

	static void clear() {
		champsParType.clear();
	}

	public static FieldInformations getChampByName(final Class<?> typeObjetParent, final String name) {
		final Champ champ = getChamps(typeObjetParent).champsParNom.get(name);
		if (champ != null)
			return champ;
		if (ConfigurationMarshalling.isModelContraignant())
			throw new ChampNotFound(
					"le champ " + name + " n'existe pas dans l'objet de type " + typeObjetParent.getName());
		return NullChamp.getInstance();
	}

	public static Champ getChampId(final Class<?> typeObjetParent) {
		return getChamps(typeObjetParent).champId;
	}

	private static ChampsDuType getChamps(final Class<?> typeObj) {
		var champs = champsParType.get(typeObj);
		if (champs == null) {
			// calcul hors verrou : si deux threads calculent en même temps, le premier
			// résultat enregistré est conservé.
			champs = calculeChamps(typeObj);
			final var existant = champsParType.putIfAbsent(typeObj, champs);
			if (existant != null)
				champs = existant;
		}
		return champs;
	}

	/**
	 * @return la classe sous laquelle sérialiser l'objet : pour une constante
	 *         d'énumération avec un corps (dont la classe est une sous-classe
	 *         anonyme), la classe de l'énumération.
	 */
	public static Class<?> getClasseASerialiser(final Object o) {
		if (o instanceof Enum)
			return ((Enum<?>) o).getDeclaringClass();
		return o.getClass();
	}

	/**
	 * @return true pour une énumération ou la sous-classe anonyme d'une de ses
	 *         constantes.
	 */
	public static boolean isEnum(final Class<?> type) {
		return Enum.class.isAssignableFrom(type) && type != Enum.class;
	}

	/**
	 * @return les constantes d'une énumération, dans l'ordre des ordinaux. Le
	 *         tableau est partagé : ne pas le modifier.
	 */
	@SuppressWarnings("unchecked")
	public static <E> E[] getEnumConstants(final Class<?> typeEnum) {
		return (E[]) enumConstants.get(typeEnum);
	}

	/**
	 * @return les constantes d'une énumération indexées par leur toString().
	 */
	@SuppressWarnings("unchecked")
	public static <E> Map<String, E> getEnumParNom(final Class<?> typeEnum) {
		return (Map<String, E>) enumParNom.computeIfAbsent(typeEnum, t -> {
			final Map<String, Enum<?>> dico = new HashMap<>();
			for (final Object objEnum : getEnumConstants(t))
				dico.put(objEnum.toString(), (Enum<?>) objEnum);
			return Collections.unmodifiableMap(dico);
		});
	}

	public static List<Champ> getSerializableFields(final Class<?> typeObj) {
		return getChamps(typeObj).champs;
	}

	/**
	 * @return les champs sérialisables hors champ id, dans l'ordre de
	 *         {@link #getSerializableFields(Class)}.
	 */
	public static List<Champ> getSerializableFieldsSaufId(final Class<?> typeObj) {
		return getChamps(typeObj).champsSaufId;
	}

	public static Class<?> getTypeEnveloppe(final Class<?> typePrimitif) {
		if (typePrimitif == null || !typePrimitif.isPrimitive())
			return typePrimitif;
		return dicoTypePrimitifToEnveloppe.get(typePrimitif);
	}

	public static boolean isEnveloppe(final Class<?> clazz) {
		return simpleEnveloppe.contains(clazz);
	}

	/**
	 * @return true si la classe est une classe technique Hibernate (proxy,
	 *         collection persistante...). Résultat mis en cache par classe.
	 */
	public static boolean isHibernate(final Class<?> type) {
		return hibernate.get(type);
	}

	public static boolean isSimple(final Class<?> type) { // Simple types become XML Attributes and JSON Values
		return type.isPrimitive() || isEnum(type) || simpleTypes.contains(type);
	}

	public static boolean isSimpleBinary(final Class<?> clazz) {
		return clazz.isPrimitive() || simpleEnveloppe.contains(clazz) || isEnum(clazz);
	}

	private static boolean isTransient(final Field info) {
		return info.getAnnotation(ConfigurationMarshalling.getAnnotationIgnoreSerialise()) != null;
	}

	private TypeExtension() {
		// privateconstructor to hide explicit public one
	}
}
