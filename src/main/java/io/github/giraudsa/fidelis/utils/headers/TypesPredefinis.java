package io.github.giraudsa.fidelis.utils.headers;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Classes courantes du JDK ayant un numéro de type fixe dans le format binaire, connu de l'écrivain comme du lecteur
 * : leur nom n'est jamais écrit. Les types rencontrés en cours de flux sont numérotés à la suite.
 */
public final class TypesPredefinis {
	/** index = numéro de type (0 inutilisé). L'ordre fait partie du format : ne pas le modifier, seulement ajouter. */
	private static final Class<?>[] CLASSES = { null, ArrayList.class, LinkedList.class, HashMap.class,
			LinkedHashMap.class, TreeMap.class, HashSet.class, LinkedHashSet.class, TreeSet.class,
			ConcurrentHashMap.class, CopyOnWriteArrayList.class, ArrayDeque.class };

	/** premier numéro libre pour les types rencontrés en cours de flux. */
	public static final short PREMIER_LIBRE = (short) CLASSES.length;

	private static final ClassValue<Short> NUMEROS = new ClassValue<>() {
		@Override
		protected Short computeValue(final Class<?> type) {
			for (short i = 1; i < CLASSES.length; i++)
				if (CLASSES[i] == type)
					return i;
			return 0;
		}
	};

	/** @return le numéro fixe de la classe, ou 0 si elle n'en a pas. */
	public static short numero(final Class<?> type) {
		return NUMEROS.get(type);
	}

	/** @return la classe d'un numéro fixe (1 ≤ numero &lt; PREMIER_LIBRE). */
	public static Class<?> classe(final short numero) {
		return CLASSES[numero];
	}

	private TypesPredefinis() {
	}
}
