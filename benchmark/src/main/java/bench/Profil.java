package bench;

import bench.model.Catalogue;

/** Boucle d'une phase (serialisation|deserialisation) pour profilage JFR. */
public final class Profil {
	public static void main(final String[] args) throws Exception {
		final Codec codec = Codec.cree(args[0]);
		final boolean ser = "serialisation".equals(args[1]);
		final long duree = Long.parseLong(args[2]) * 1000;
		final Catalogue c = args.length > 3 && "petit".equals(args[3]) ? Catalogue.genere(1, 10) : Catalogue.genere(1000, 10);
		final Object data = codec.encode(c);
		long n = 0;
		final long fin = System.currentTimeMillis() + duree;
		while (System.currentTimeMillis() < fin) {
			if (ser)
				codec.encode(c);
			else
				codec.decode(data);
			n++;
		}
		System.out.println(n + " itérations");
	}
}
