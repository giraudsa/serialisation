package giraudsa.marshall.deserialisation.binary;

import java.util.ArrayList;

/**
 * Table indexée par un petit identifiant positif, attribué séquentiellement :
 * remplace une Map&lt;Integer, T&gt; sans boxing ni hachage.
 */
final class TableParId<T> {
	private final ArrayList<T> valeurs = new ArrayList<>();

	boolean contient(final int id) {
		return id >= 0 && id < valeurs.size() && valeurs.get(id) != null;
	}

	T get(final int id) {
		return id >= 0 && id < valeurs.size() ? valeurs.get(id) : null;
	}

	void set(final int id, final T valeur) {
		while (valeurs.size() <= id)
			valeurs.add(null);
		valeurs.set(id, valeur);
	}
}
