package io.github.giraudsa.fidelis.deserialisation.binary;

import java.util.Arrays;

/**
 * Table indexée par un petit identifiant positif, attribué séquentiellement :
 * remplace une Map&lt;Integer, T&gt; sans boxing ni hachage.
 */
final class TableParId<T> {
	private Object[] valeurs = new Object[64];
	/** plus grand id affecté + 1 : borne de ce qu'il faut effacer pour réutiliser la table. */
	private int haut;

	boolean contient(final int id) {
		return get(id) != null;
	}

	@SuppressWarnings("unchecked")
	T get(final int id) {
		return id >= 0 && id < valeurs.length ? (T) valeurs[id] : null;
	}

	void set(final int id, final T valeur) {
		final Object[] t = valeurs;
		if (id < t.length) {
			t[id] = valeur;
			if (id >= haut)
				haut = id + 1;
		}
		else
			agranditEtSet(id, valeur); // hors du chemin courant, pour que set reste inlinable
	}

	private void agranditEtSet(final int id, final T valeur) {
		valeurs = Arrays.copyOf(valeurs, Math.max(id + 1, valeurs.length * 2));
		valeurs[id] = valeur;
		haut = id + 1;
	}

	/** Vide la table pour la réutiliser (sans retenir les objets lus) ; une table très grande est réallouée. */
	void vide() {
		if (valeurs.length > 1 << 16)
			valeurs = new Object[64];
		else
			Arrays.fill(valeurs, 0, haut, null);
		haut = 0;
	}
}
