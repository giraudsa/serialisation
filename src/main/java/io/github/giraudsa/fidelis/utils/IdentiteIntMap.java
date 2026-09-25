package io.github.giraudsa.fidelis.utils;

import java.util.Arrays;

/**
 * Table objet → int à adressage ouvert, comparaison par identité : remplace {@code IdentityHashMap<Object, Integer>}
 * sans boxing ni entrée allouée par objet.
 */
public final class IdentiteIntMap {
	public static final int ABSENT = Integer.MIN_VALUE;
	private static final int CAPACITE_MAX_CONSERVEE = 1 << 18; // au plus ~3 Mo retenus par table

	private static int indice(final Object cle, final int masque) {
		final int h = System.identityHashCode(cle);
		// les hash d'identité sont souvent proches : on les mélange (Fibonacci)
		final int x = h * 0x9E3779B9;
		return (x ^ x >>> 16) & masque;
	}

	private final int capaciteInitiale;
	private Object[] cles;
	private int masque;
	private int taille;
	private int[] valeurs;
	/**
	 * cases occupées, dans l'ordre d'insertion, tant que la table est peu remplie : vide() n'efface alors que ces
	 * cases au lieu de tout le tableau (coût fixe important pour un petit graphe). null au-delà.
	 */
	private int[] occupees;

	public IdentiteIntMap() {
		this(64);
	}

	public IdentiteIntMap(final int capaciteInitiale) {
		this.capaciteInitiale = Integer.highestOneBit(Math.max(4, capaciteInitiale) * 2 - 1);
		alloue(this.capaciteInitiale);
	}

	private void alloue(final int capacite) {
		cles = new Object[capacite];
		valeurs = new int[capacite];
		masque = capacite - 1;
		occupees = new int[Math.max(4, capacite >> 3)];
	}

	/** Vide la table pour la réutiliser ; une table devenue très grande est réallouée à sa taille initiale. */
	public void vide() {
		if (cles.length > CAPACITE_MAX_CONSERVEE)
			alloue(capaciteInitiale);
		else if (occupees != null)
			for (int i = 0; i < taille; i++)
				cles[occupees[i]] = null;
		else if (taille > 0)
			Arrays.fill(cles, null);
		if (occupees == null)
			occupees = new int[Math.max(4, cles.length >> 3)];
		taille = 0;
	}

	public boolean contient(final Object cle) {
		return get(cle) != ABSENT;
	}

	/** @return la valeur associée ou {@link #ABSENT}. */
	public int get(final Object cle) {
		final Object[] t = cles;
		int i = indice(cle, masque);
		while (true) {
			final Object c = t[i];
			if (c == cle)
				return valeurs[i];
			if (c == null)
				return ABSENT;
			i = i + 1 & masque;
		}
	}

	/** Associe la valeur si la clé est absente. @return la valeur existante, ou {@link #ABSENT} si ajoutée. */
	public int putIfAbsent(final Object cle, final int valeur) {
		final Object[] t = cles;
		int i = indice(cle, masque);
		while (true) {
			final Object c = t[i];
			if (c == cle)
				return valeurs[i];
			if (c == null) {
				t[i] = cle;
				valeurs[i] = valeur;
				noteOccupee(i);
				if (++taille * 2 > t.length)
					agrandit();
				return ABSENT;
			}
			i = i + 1 & masque;
		}
	}

	/**
	 * Ajoute les bits à la valeur de la clé (valeur 0 si absente). @return la valeur précédente, ou {@link #ABSENT}.
	 */
	public int ou(final Object cle, final int bits) {
		final Object[] t = cles;
		int i = indice(cle, masque);
		while (true) {
			final Object c = t[i];
			if (c == cle) {
				final int precedente = valeurs[i];
				valeurs[i] = precedente | bits;
				return precedente;
			}
			if (c == null) {
				t[i] = cle;
				valeurs[i] = bits;
				noteOccupee(i);
				if (++taille * 2 > t.length)
					agrandit();
				return ABSENT;
			}
			i = i + 1 & masque;
		}
	}

	private void noteOccupee(final int i) {
		final int[] o = occupees;
		if (o != null) {
			if (taille < o.length)
				o[taille] = i;
			else
				occupees = null; // table trop remplie : vide() effacera tout le tableau
		}
	}

	private void agrandit() {
		final Object[] anciennesCles = cles;
		final int[] anciennesValeurs = valeurs;
		alloue(anciennesCles.length * 2);
		for (int j = 0; j < anciennesCles.length; j++) {
			final Object cle = anciennesCles[j];
			if (cle != null) {
				int i = indice(cle, masque);
				while (cles[i] != null)
					i = i + 1 & masque;
				cles[i] = cle;
				valeurs[i] = anciennesValeurs[j];
			}
		}
		occupees = null; // positions changées : le journal n'est plus valable
	}
}
