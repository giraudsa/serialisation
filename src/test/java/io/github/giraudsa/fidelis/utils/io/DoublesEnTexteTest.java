package io.github.giraudsa.fidelis.utils.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.nio.charset.StandardCharsets;
import java.util.SplittableRandom;

import org.junit.jupiter.api.Test;

/** L'écriture directe doit donner exactement Double.toString (JDK 19 et suivants). */
class DoublesEnTexteTest {

	private static final byte[] TAMPON = new byte[DoublesEnTexte.LONGUEUR_MAX];

	private static void verifie(final double v) {
		final int fin = DoublesEnTexte.ecris(v, TAMPON, 0);
		if (fin < 0)
			return;
		assertEquals(Double.toString(v), new String(TAMPON, 0, fin, StandardCharsets.ISO_8859_1),
				() -> "bits " + Long.toHexString(Double.doubleToRawLongBits(v)));
	}

	@Test
	void identiqueADoubleToString() {
		// avant le JDK 19, Double.toString suit un autre algorithme : l'écriture directe n'est pas utilisée
		assumeTrue(DoublesEnTexte.DISPONIBLE, "JDK 19+ requis");
		final SplittableRandom r = new SplittableRandom(11);
		for (int i = 0; i < 3_000_000; i++) {
			verifie(Double.longBitsToDouble(r.nextLong()));
			verifie(r.nextDouble());
			verifie(r.nextDouble() * Math.pow(10, r.nextInt(40) - 20));
			verifie(Math.round(r.nextDouble() * 1e6) / Math.pow(10, r.nextInt(10)));
			verifie(r.nextInt() / 1000.0);
		}
		// bornes, puissances, entiers, limites de mise en forme
		for (int e = -1074; e <= 1023; e++) {
			final double p = Math.scalb(1.0, e);
			verifie(p);
			verifie(Math.nextUp(p));
			verifie(Math.nextDown(p));
			verifie(-p);
		}
		for (int e = -325; e <= 308; e++) {
			final double p = Double.parseDouble("1e" + e);
			verifie(p);
			verifie(Math.nextUp(p));
			verifie(Math.nextDown(p));
		}
		for (final double v : new double[] { 1, 0.1, 0.001, 0.0001, 1e7, 9999999.0, 1e7 - 0.5, 1.5, 100, 1e21, 1e22,
				1e23, 2e-3, 9.999e-4, Double.MAX_VALUE, Double.MIN_NORMAL, 4.9e-324, Math.PI, Math.E, 1.0 / 3,
				2.0 / 3, 123456789012345678.0, 0.3, 0.1 + 0.2 })
			verifie(v);
		final byte[] b = new byte[30];
		for (final double v : new double[] { 0.0, -0.0, Double.NaN, Double.POSITIVE_INFINITY,
				Double.NEGATIVE_INFINITY, 4.9e-324, 1e-310 })
			assertEquals(-1, DoublesEnTexte.ecris(v, b, 0), Double.toString(v));
	}
}
