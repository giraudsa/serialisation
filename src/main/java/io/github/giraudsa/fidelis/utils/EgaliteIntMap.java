package io.github.giraudsa.fidelis.utils;

import java.util.Arrays;

/**
 * Table objet → int à adressage ouvert, comparaison par equals : remplace {@code HashMap<K, Integer>} sans boxing ni
 * entrée allouée par clé. Les valeurs doivent être non nulles (> 0 : ce sont des smallIds).
 * <p>
 * Le hash et la valeur de chaque case sont rangés côte à côte dans un même tableau d'int : un sondage ne touche
 * qu'une ligne de cache, et la clé n'est lue (equals) qu'en cas de hash égal.
 */
public final class EgaliteIntMap {
	private static final int CAPACITE_MAX_CONSERVEE = 1 << 18; // au plus ~3 Mo retenus par table

	private static int melange(final int h) {
		// mélange (Fibonacci) : les hashCode de String ou Date sont mal répartis sur les bits faibles
		final int x = h * 0x9E3779B9;
		return x ^ x >>> 16;
	}

	private final int capaciteInitiale;
	private Object[] cles;
	/** [2i] = hash de la case i, [2i + 1] = valeur (0 : case vide). */
	private int[] hashsEtValeurs;
	private int masque;
	/**
	 * cases occupées, dans l'ordre d'insertion, tant que la table est peu remplie : vide() n'efface alors que ces
	 * cases au lieu de tout le tableau (coût fixe important pour un petit graphe). null au-delà.
	 */
	private int[] occupees;
	private int taille;

	public EgaliteIntMap() {
		this(64);
	}

	public EgaliteIntMap(final int capaciteInitiale) {
		this.capaciteInitiale = Integer.highestOneBit(Math.max(4, capaciteInitiale) * 2 - 1);
		alloue(this.capaciteInitiale);
	}

	private void alloue(final int capacite) {
		cles = new Object[capacite];
		hashsEtValeurs = new int[capacite * 2];
		masque = capacite - 1;
		occupees = new int[Math.max(4, capacite >> 1)]; // une table est au plus à moitié pleine
	}

	/**
	 * Associe la valeur (> 0) si la clé est absente. @return la valeur existante, ou {@link IdentiteIntMap#ABSENT}
	 * si la clé vient d'être ajoutée.
	 */
	public int putIfAbsent(final Object cle, final int valeur) {
		final int h = melange(cle.hashCode());
		final int[] hv = hashsEtValeurs;
		int i = h & masque;
		while (true) {
			final int v = hv[2 * i + 1];
			if (v == 0) {
				cles[i] = cle;
				hv[2 * i] = h;
				hv[2 * i + 1] = valeur;
				noteOccupee(i);
				if (++taille * 2 > cles.length)
					agrandit();
				return IdentiteIntMap.ABSENT;
			}
			if (hv[2 * i] == h) {
				final Object c = cles[i];
				if (c == cle || c.equals(cle))
					return v;
			}
			i = i + 1 & masque;
		}
	}

	/** Vide la table pour la réutiliser ; une table devenue très grande est réallouée à sa taille initiale. */
	public void vide() {
		if (cles.length > CAPACITE_MAX_CONSERVEE)
			alloue(capaciteInitiale);
		else if (occupees != null)
			for (int k = 0; k < taille; k++) {
				final int i = occupees[k];
				cles[i] = null;
				hashsEtValeurs[2 * i + 1] = 0;
			}
		else if (taille > 0) {
			Arrays.fill(cles, null);
			Arrays.fill(hashsEtValeurs, 0);
		}
		if (occupees == null)
			occupees = new int[Math.max(4, cles.length >> 1)];
		taille = 0;
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
		final int[] anciens = hashsEtValeurs;
		alloue(anciennesCles.length * 2);
		for (int j = 0; j < anciennesCles.length; j++) {
			final int v = anciens[2 * j + 1];
			if (v != 0) {
				final int h = anciens[2 * j];
				int i = h & masque;
				while (hashsEtValeurs[2 * i + 1] != 0)
					i = i + 1 & masque;
				cles[i] = anciennesCles[j];
				hashsEtValeurs[2 * i] = h;
				hashsEtValeurs[2 * i + 1] = v;
			}
		}
		// positions changées : on reconstruit le journal
		int k = 0;
		for (int i = 0; i < cles.length; i++)
			if (hashsEtValeurs[2 * i + 1] != 0)
				occupees[k++] = i;
	}
}
