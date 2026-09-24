package utils;

import java.util.Arrays;

/**
 * Table objet → int à adressage ouvert, comparaison par equals : remplace {@code HashMap<K, Integer>} sans boxing ni
 * entrée allouée par clé. Le hash de chaque clé est conservé pour n'appeler equals qu'en cas de hash égal.
 */
public final class EgaliteIntMap {
	private static final int CAPACITE_MAX_CONSERVEE = 1 << 16;

	private static int melange(final int h) {
		// mélange (Fibonacci) : les hashCode de String ou Date sont mal répartis sur les bits faibles
		final int x = h * 0x9E3779B9;
		return x ^ x >>> 16;
	}

	private final int capaciteInitiale;
	private Object[] cles;
	private int[] hashs;
	private int masque;
	private int taille;
	private int[] valeurs;
	/**
	 * cases occupées, dans l'ordre d'insertion, tant que la table est peu remplie : vide() n'efface alors que ces
	 * cases au lieu de tout le tableau (coût fixe important pour un petit graphe). null au-delà.
	 */
	private int[] occupees;

	public EgaliteIntMap() {
		this(64);
	}

	public EgaliteIntMap(final int capaciteInitiale) {
		this.capaciteInitiale = Integer.highestOneBit(Math.max(4, capaciteInitiale) * 2 - 1);
		alloue(this.capaciteInitiale);
	}

	private void alloue(final int capacite) {
		cles = new Object[capacite];
		hashs = new int[capacite];
		valeurs = new int[capacite];
		masque = capacite - 1;
		occupees = new int[Math.max(4, capacite >> 3)];
	}

	/** Associe la valeur si la clé est absente. @return la valeur existante, ou {@link IdentiteIntMap#ABSENT}. */
	public int putIfAbsent(final Object cle, final int valeur) {
		final int h = melange(cle.hashCode());
		final Object[] t = cles;
		int i = h & masque;
		while (true) {
			final Object c = t[i];
			if (c == null) {
				t[i] = cle;
				hashs[i] = h;
				valeurs[i] = valeur;
				noteOccupee(i);
				if (++taille * 2 > t.length)
					agrandit();
				return IdentiteIntMap.ABSENT;
			}
			if (c == cle || hashs[i] == h && c.equals(cle))
				return valeurs[i];
			i = i + 1 & masque;
		}
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
		final int[] anciensHashs = hashs;
		final int[] anciennesValeurs = valeurs;
		alloue(anciennesCles.length * 2);
		for (int j = 0; j < anciennesCles.length; j++) {
			final Object cle = anciennesCles[j];
			if (cle != null) {
				int i = anciensHashs[j] & masque;
				while (cles[i] != null)
					i = i + 1 & masque;
				cles[i] = cle;
				hashs[i] = anciensHashs[j];
				valeurs[i] = anciennesValeurs[j];
			}
		}
		occupees = null; // positions changées : le journal n'est plus valable
	}
}
