package io.github.giraudsa.fidelis.utils.io;

import java.math.BigInteger;

/**
 * Écriture d'un double en ASCII, sans chaîne intermédiaire, identique à {@link Double#toString(double)} du JDK 19 et
 * suivants : décimal le plus court qui se relit en la même valeur (le plus proche, pair en cas d'égalité), mis en
 * forme selon la spécification (notation décimale de 10^-3 à 10^7, scientifique sinon, au moins un chiffre après le
 * point). Algorithme Schubfach (R. Giulietti, « The Schubfach way to render doubles »), le même que le JDK.
 * <p>
 * Avant le JDK 19, Double.toString suivait un autre algorithme : l'écriture directe n'est alors pas utilisée. Zéro,
 * sous-normaux, infinis et NaN sont laissés à Double.toString.
 */
public final class DoublesEnTexte {

	/** l'écriture directe donne le même texte que Double.toString de ce JDK. */
	public static final boolean DISPONIBLE = Runtime.version().feature() >= 19;

	/** longueur maximale d'un double écrit ("-2.2250738585072014E-308"). */
	public static final int LONGUEUR_MAX = 24;

	private static final int P = 53;
	private static final int Q_MIN = -1074;
	private static final long C_MIN = 1L << P - 1;
	private static final int H = 17;
	private static final long MASQUE_63 = (1L << 63) - 1;
	private static final int MASQUE_28 = (1 << 28) - 1;
	private static final int K_MIN = -324;
	private static final int K_MAX = 292;

	/** g = floor(10^-k 2^-r) + 1 dans [2^125, 2^126[, r = flog2pow10(-k) - 125 : 63 bits hauts et bas, par k. */
	private static final class Table {
		private static final long[] G1 = new long[K_MAX - K_MIN + 1];
		private static final long[] G0 = new long[K_MAX - K_MIN + 1];

		static {
			final BigInteger masque = BigInteger.ONE.shiftLeft(63).subtract(BigInteger.ONE);
			for (int k = K_MIN; k <= K_MAX; k++) {
				final int r = flog2pow10(-k) - 125;
				// 10^-k 2^-r = 10^-k / 2^r
				BigInteger num = BigInteger.ONE;
				BigInteger den = BigInteger.ONE;
				if (k <= 0)
					num = num.multiply(BigInteger.TEN.pow(-k));
				else
					den = den.multiply(BigInteger.TEN.pow(k));
				if (r <= 0)
					num = num.shiftLeft(-r);
				else
					den = den.shiftLeft(r);
				final BigInteger g = num.divide(den).add(BigInteger.ONE);
				G1[k - K_MIN] = g.shiftRight(63).longValue();
				G0[k - K_MIN] = g.and(masque).longValue();
			}
		}
	}

	private static final long[] PUISSANCES_10 = new long[18];

	static {
		long p = 1;
		for (int i = 0; i < PUISSANCES_10.length; i++, p *= 10)
			PUISSANCES_10[i] = p;
	}

	private DoublesEnTexte() {
	}

	private static int flog10pow2(final int e) {
		return (int) (e * 661_971_961_083L >> 41);
	}

	private static int flog10threeQuartersPow2(final int e) {
		return (int) (e * 661_971_961_083L + -274_743_187_321L >> 41);
	}

	private static int flog2pow10(final int e) {
		return (int) (e * 913_124_641_741L >> 38);
	}


	/** arrondi impair du produit de g (g1 2^63 + g0) par cp, décalé (voir l'article). */
	private static long rop(final long g1, final long g0, final long cp) {
		final long x1 = Math.multiplyHigh(g0, cp);
		final long y0 = g1 * cp;
		final long y1 = Math.multiplyHigh(g1, cp);
		final long z = (y0 >>> 1) + x1;
		final long vbp = y1 + (z >>> 63);
		return vbp | (z & MASQUE_63) + MASQUE_63 >>> 63;
	}

	/**
	 * Écrit v à partir de b[p].
	 *
	 * @return la position après le texte, ou -1 si v est laissé à Double.toString (zéro, sous-normal, infini, NaN, ou
	 *         JDK antérieur au 19)
	 */
	public static int ecris(final double v, final byte[] b, final int p) {
		if (!DISPONIBLE)
			return -1;
		final long bits = Double.doubleToRawLongBits(v);
		final int bq = (int) (bits >>> P - 1) & 0x7FF;
		if (bq == 0 || bq == 0x7FF)
			return -1;
		int i = p;
		if (bits < 0)
			b[i++] = '-';
		final int mq = -Q_MIN + 1 - bq;
		final long c = C_MIN | bits & C_MIN - 1;
		if (0 < mq && mq < P) {
			final long f = c >> mq;
			if (f << mq == c)
				return chiffres(f, 0, b, i);
		}
		return decimal(-mq, c, b, i);
	}

	private static int decimal(final int q, final long c, final byte[] b, final int i) {
		final int out = (int) c & 0x1;
		final long cb = c << 2;
		final long cbr = cb + 2;
		final long cbl;
		final int k;
		if (c != C_MIN || q == Q_MIN) {
			cbl = cb - 2;
			k = flog10pow2(q);
		} else {
			cbl = cb - 1;
			k = flog10threeQuartersPow2(q);
		}
		final int h = q + flog2pow10(-k) + 2;
		final long g1 = Table.G1[k - K_MIN];
		final long g0 = Table.G0[k - K_MIN];
		final long vb = rop(g1, g0, cb << h);
		final long vbl = rop(g1, g0, cbl << h);
		final long vbr = rop(g1, g0, cbr << h);
		final long s = vb >> 2;
		if (s >= 100) {
			final long sp10 = 10 * Math.multiplyHigh(s, 115_292_150_460_684_698L << 4);
			final long tp10 = sp10 + 10;
			final boolean upin = vbl + out <= sp10 << 2;
			final boolean wpin = (tp10 << 2) + out <= vbr;
			if (upin != wpin)
				return chiffres(upin ? sp10 : tp10, k, b, i);
		}
		final long t = s + 1;
		final boolean uin = vbl + out <= s << 2;
		final boolean win = (t << 2) + out <= vbr;
		if (uin != win)
			return chiffres(uin ? s : t, k, b, i);
		final long cmp = vb - (s + t << 1);
		return chiffres(cmp < 0 || cmp == 0 && (s & 0x1) == 0 ? s : t, k, b, i);
	}

	/** écrit f 10^e selon la mise en forme de Double.toString. */
	private static int chiffres(final long f0, final int e0, final byte[] b, final int debut) {
		int len = flog10pow2(Long.SIZE - Long.numberOfLeadingZeros(f0));
		if (f0 >= PUISSANCES_10[len])
			len += 1;
		final long f = f0 * PUISSANCES_10[H - len];
		final int e = e0 + len;
		final long hm = Math.multiplyHigh(f, 193_428_131_138_340_668L) >>> 20;
		final int l = (int) (f - 100_000_000L * hm);
		final int hh = (int) (hm * 1_441_151_881L >>> 57);
		final int m = (int) (hm - 100_000_000 * hh);
		if (0 < e && e <= 7)
			return decimalSansExposant(hh, m, l, e, b, debut);
		if (-3 < e && e <= 0)
			return petitDecimal(hh, m, l, e, b, debut);
		return scientifique(hh, m, l, e, b, debut);
	}

	/** y tel que les chiffres de m (8 chiffres) sortent de gauche à droite par multiplications par 10. */
	private static int y(final int m) {
		return (int) (Math.multiplyHigh((long) (m + 1) << 28, 193_428_131_138_340_668L) >>> 20) - 1;
	}

	private static int decimalSansExposant(final int h, final int m, final int l, final int e, final byte[] b,
			final int debut) {
		int i = debut;
		b[i++] = (byte) ('0' + h);
		int y = y(m);
		int t;
		int j = 1;
		for (; j < e; ++j) {
			t = 10 * y;
			b[i++] = (byte) ('0' + (t >>> 28));
			y = t & MASQUE_28;
		}
		b[i++] = '.';
		for (; j <= 8; ++j) {
			t = 10 * y;
			b[i++] = (byte) ('0' + (t >>> 28));
			y = t & MASQUE_28;
		}
		return chiffresBas(l, b, i);
	}

	private static int petitDecimal(final int h, final int m, final int l, final int e0, final byte[] b,
			final int debut) {
		int i = debut;
		b[i++] = '0';
		b[i++] = '.';
		for (int e = e0; e < 0; ++e)
			b[i++] = '0';
		b[i++] = (byte) ('0' + h);
		i = huitChiffres(m, b, i);
		return chiffresBas(l, b, i);
	}

	private static int scientifique(final int h, final int m, final int l, final int e, final byte[] b,
			final int debut) {
		int i = debut;
		b[i++] = (byte) ('0' + h);
		b[i++] = '.';
		i = huitChiffres(m, b, i);
		i = chiffresBas(l, b, i);
		return exposant(e - 1, b, i);
	}

	private static int huitChiffres(final int m, final byte[] b, final int debut) {
		int i = debut;
		int y = y(m);
		for (int j = 0; j < 8; ++j) {
			final int t = 10 * y;
			b[i++] = (byte) ('0' + (t >>> 28));
			y = t & MASQUE_28;
		}
		return i;
	}

	/** les 8 derniers chiffres s'il n'y a pas que des zéros, puis retrait des zéros finals (un chiffre après le point). */
	private static int chiffresBas(final int l, final byte[] b, final int debut) {
		int i = debut;
		if (l != 0)
			i = huitChiffres(l, b, i);
		// i : position après le dernier caractère ; retire les zéros de fin, garde un chiffre après le point
		int dernier = i - 1;
		while (b[dernier] == '0')
			--dernier;
		if (b[dernier] == '.')
			++dernier;
		return dernier + 1;
	}

	private static int exposant(final int e0, final byte[] b, final int debut) {
		int i = debut;
		int e = e0;
		b[i++] = 'E';
		if (e < 0) {
			b[i++] = '-';
			e = -e;
		}
		if (e < 10) {
			b[i++] = (byte) ('0' + e);
			return i;
		}
		int d;
		if (e >= 100) {
			d = e * 1_311 >>> 17;
			b[i++] = (byte) ('0' + d);
			e -= 100 * d;
		}
		d = e * 103 >>> 10;
		b[i++] = (byte) ('0' + d);
		b[i++] = (byte) ('0' + e - 10 * d);
		return i;
	}
}
