package bench;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import bench.model.Catalogue;

/**
 * Vérifie que chaque framework relit un catalogue identique à l'original (comparaison d'un dump Jackson canonique)
 * et affiche la taille de la forme sérialisée.
 */
public final class Verification {
	public static void main(final String[] args) throws Exception {
		final ObjectMapper dump = Codec.jacksonChamps(new ObjectMapper())
				.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
		for (final int n : new int[] { 1, 1000 }) {
			final Catalogue c = Catalogue.genere(n, 10);
			final String attendu = dump.writeValueAsString(c);
			System.out.printf("%n== %d commande(s) ==%n", n);
			for (final String nom : Codec.NOMS) {
				try {
					final Codec codec = Codec.cree(nom);
					final Object data = codec.encode(c);
					final boolean ok = attendu.equals(dump.writeValueAsString(codec.decode(data)));
					System.out.printf("%-18s %10d octets  %s%n", nom, Codec.taille(data), ok ? "OK" : "DIFFÉRENT");
				} catch (final Exception | Error e) {
					System.out.printf("%-18s ÉCHEC : %s%n", nom, e);
				}
			}
		}
	}
}
