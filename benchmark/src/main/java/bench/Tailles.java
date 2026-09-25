package bench;

import bench.model.Catalogue;

/** Coût marginal (octets) d'une commande et d'une ligne, par différence de tailles. */
public final class Tailles {
	public static void main(final String[] args) throws Exception {
		for (final String nom : new String[] { "giraudsa-binaire", "kryo", "java-natif" }) {
			final Codec c = Codec.cree(nom);
			final int base = Codec.taille(c.encode(Catalogue.genere(1000, 0)));
			final int doubleCmd = Codec.taille(c.encode(Catalogue.genere(2000, 0)));
			final int avecLignes = Codec.taille(c.encode(Catalogue.genere(1000, 10)));
			final int vide = Codec.taille(c.encode(Catalogue.genere(0, 0)));
			System.out.printf("%-17s catalogue vide %4d o | commande sans ligne %6.1f o | ligne %6.1f o%n", nom, vide,
					(doubleCmd - base) / 1000.0, (avecLignes - base) / 10000.0);
		}
	}
}
