package utils.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.SplittableRandom;

import org.junit.jupiter.api.Test;

/** La lecture rapide doit rendre exactement Double.parseDouble, ou renoncer. */
class DecimauxTest {

	private static int rapides;
	private static int total;

	private static void verifie(final String s) {
		final byte[] b = s.getBytes(StandardCharsets.ISO_8859_1);
		final double v = Decimaux.lit(b, 0, b.length);
		total++;
		if (Double.isNaN(v))
			return;
		rapides++;
		assertEquals(Double.doubleToRawLongBits(Double.parseDouble(s)), Double.doubleToRawLongBits(v), s);
	}

	@Test
	void doublesAleatoires() {
		rapides = total = 0;
		final SplittableRandom r = new SplittableRandom(42);
		for (int i = 0; i < 1_000_000; i++) {
			final double d = Double.longBitsToDouble(r.nextLong());
			if (Double.isNaN(d) || Double.isInfinite(d))
				continue;
			verifie(Double.toString(d));
			verifie(new BigDecimal(d).round(new java.math.MathContext(1 + r.nextInt(19))).toString());
		}
		for (int i = 0; i < 1_000_000; i++) {
			final double d = r.nextDouble() * Math.pow(10, r.nextInt(40) - 20);
			verifie(Double.toString(d));
			verifie(Double.toString(-d));
		}
		assertTrue(rapides > total * 9 / 10, rapides + " / " + total);
	}

	@Test
	void chiffresAleatoires() {
		final SplittableRandom r = new SplittableRandom(7);
		for (int i = 0; i < 1_000_000; i++) {
			final StringBuilder sb = new StringBuilder();
			if (r.nextBoolean())
				sb.append('-');
			final int entiers = 1 + r.nextInt(12);
			for (int k = 0; k < entiers; k++)
				sb.append((char) ('0' + r.nextInt(10)));
			if (r.nextBoolean()) {
				sb.append('.');
				final int decimales = 1 + r.nextInt(12);
				for (int k = 0; k < decimales; k++)
					sb.append((char) ('0' + r.nextInt(10)));
			}
			if (r.nextInt(3) == 0)
				sb.append(r.nextBoolean() ? 'e' : 'E').append(r.nextBoolean() ? "-" : r.nextBoolean() ? "+" : "")
						.append(r.nextInt(360));
			verifie(sb.toString());
		}
	}

	@Test
	void milieuxEtBornes() {
		// milieux exacts entre deux doubles (entiers au-delà de 2^53), et leurs voisins
		final SplittableRandom r = new SplittableRandom(3);
		for (int i = 0; i < 300_000; i++) {
			final long bits = (1076L + r.nextInt(10) << 52) + (r.nextLong() & (1L << 52) - 1);
			final BigInteger d = new BigDecimal(Double.longBitsToDouble(bits)).toBigInteger();
			final BigInteger suivant = new BigDecimal(Math.nextUp(Double.longBitsToDouble(bits))).toBigInteger();
			final BigInteger milieu = d.add(suivant).shiftRight(1);
			for (int k = -1; k <= 1; k++) {
				final String s = milieu.add(BigInteger.valueOf(k)).toString();
				verifie(s);
				verifie(s + "000");
				verifie(s.substring(0, s.length() - 3) + "." + s.substring(s.length() - 3) + "e3");
			}
		}
		for (final String s : new String[] { "0", "-0", "0.0", "-0.0", "0e999", "1", "-1", "9007199254740992",
				"9007199254740993", "9007199254740994", "9007199254740995", "18446744073709551615",
				"9999999999999999999", "1e22", "1e23", "1.7976931348623157e308", "1.7976931348623158e308",
				"1.7976931348623159e308", "2.2250738585072014E-308", "2.2250738585072011E-308", "4.9e-324",
				"1e-342", "1e308", "1e309", "123.456e-2", "0.000000000000000000001", "1E+2", "00012.5000" })
			verifie(s);
		for (final String s : new String[] { "", "-", ".5", "5.", "1e", "1e+", "1ee2", "1.2.3", "+1", "1d", "NaN",
				"Infinity", "0x1p3", "1e12345", "12345678901234567890" }) {
			final byte[] b = s.getBytes(StandardCharsets.ISO_8859_1);
			assertTrue(Double.isNaN(Decimaux.lit(b, 0, b.length)), s);
		}
	}
}
