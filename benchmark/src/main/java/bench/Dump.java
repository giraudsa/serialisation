package bench;

import bench.model.Catalogue;

public final class Dump {
	public static void main(final String[] args) throws Exception {
		for (final String nom : new String[] { "giraudsa-binaire", "kryo" }) {
			final Codec c = Codec.cree(nom);
			final byte[] a = (byte[]) c.encode(Catalogue.genere(300, 2));
			final byte[] b = (byte[]) c.encode(Catalogue.genere(300, 3));
			// les données aléatoires divergent : on affiche la fin des deux et on compare visuellement
			System.out.println(nom + " : " + a.length + " -> " + b.length);
			System.out.println(hex(b, b.length - 160, b.length));
			System.out.println(ascii(b, b.length - 160, b.length));
		}
	}

	static String hex(final byte[] t, final int d, final int f) {
		final StringBuilder sb = new StringBuilder();
		for (int i = d; i < f; i++)
			sb.append(String.format("%02x ", t[i]));
		return sb.toString();
	}

	static String ascii(final byte[] t, final int d, final int f) {
		final StringBuilder sb = new StringBuilder();
		for (int i = d; i < f; i++)
			sb.append(t[i] >= 32 && t[i] < 127 ? " " + (char) t[i] + " " : " . ");
		return sb.toString();
	}
}
