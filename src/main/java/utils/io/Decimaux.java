package utils.io;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.math.BigInteger;
import java.nio.ByteOrder;

/**
 * Lecture rapide des décimaux écrits en ASCII ([-]chiffres[.chiffres][e[+-]chiffres], au plus 19 chiffres
 * significatifs), de résultat identique à {@link Double#parseDouble(String)} (arrondi au plus proche).
 * <p>
 * Mantisse courte et petite puissance de dix : une seule opération exacte puis arrondie (Clinger). Sinon, la mantisse
 * normalisée w est multipliée par une approximation T à 128 bits de 5^q (erreur inférieure à 1) : le produit P, sur
 * 192 bits, est à moins de w &lt; 2^64 du produit exact. L'arrondi de P est donc celui du produit exact dès que P est à
 * plus de 2^64 d'un point de basculement (milieu entre deux doubles) ; sinon, comme pour les cas hors limites
 * (sous-normaux, dépassement), la lecture est laissée à Double.parseDouble.
 */
public final class Decimaux {

	/** valeur rendue quand la lecture rapide ne s'applique pas. */
	public static final double INVALIDE = Double.NaN;

	private static final int Q_MIN = -342;
	private static final int Q_MAX = 308;

	private static final double[] PUISSANCES = new double[23];

	static {
		double d = 1;
		for (int i = 0; i < PUISSANCES.length; i++, d *= 10)
			PUISSANCES[i] = d;
	}

	/** T = floor(5^q * 2^s), dans [2^127, 2^128[, par q : 64 bits hauts, 64 bits bas, et s. */
	private static final class Tables {
		private static final long[] HAUT = new long[Q_MAX - Q_MIN + 1];
		private static final long[] BAS = new long[Q_MAX - Q_MIN + 1];
		private static final int[] DECALAGE = new int[Q_MAX - Q_MIN + 1];

		static {
			final BigInteger masque = BigInteger.ONE.shiftLeft(64).subtract(BigInteger.ONE);
			final BigInteger cinq = BigInteger.valueOf(5);
			BigInteger puissance = BigInteger.ONE;
			for (int q = 0; q <= Q_MAX; q++) {
				final int s = 128 - puissance.bitLength();
				remplit(q, s >= 0 ? puissance.shiftLeft(s) : puissance.shiftRight(-s), s, masque);
				puissance = puissance.multiply(cinq);
			}
			puissance = cinq;
			for (int q = -1; q >= Q_MIN; q--) {
				final int s = 127 + puissance.bitLength();
				remplit(q, BigInteger.ONE.shiftLeft(s).divide(puissance), s, masque);
				puissance = puissance.multiply(cinq);
			}
		}

		private static void remplit(final int q, final BigInteger t, final int s, final BigInteger masque) {
			HAUT[q - Q_MIN] = t.shiftRight(64).longValue();
			BAS[q - Q_MIN] = t.and(masque).longValue();
			DECALAGE[q - Q_MIN] = s;
		}
	}

	private Decimaux() {
	}

	/** lecture de 8 octets d'un coup (API publique, sans Unsafe). */
	private static final VarHandle HUIT_OCTETS = MethodHandles.byteArrayViewVarHandle(long[].class,
			ByteOrder.LITTLE_ENDIAN);

	/** valeur des 8 chiffres ASCII de x (le premier dans l'octet de poids faible). */
	private static int huitChiffres(final long octets) {
		long x = octets - 0x3030303030303030L;
		x = x * 10 + (x >>> 8) & 0x00FF00FF00FF00FFL;
		x = x * 100 + (x >>> 16) & 0x0000FFFF0000FFFFL;
		x = x * 10000 + (x >>> 32) & 0x00000000FFFFFFFFL;
		return (int) x;
	}

	/** nombre de chiffres de v (1 à 99 999 999). */
	private static int nbChiffres(final int v) {
		if (v < 10_000)
			return v < 10 ? 1 : v < 100 ? 2 : v < 1000 ? 3 : 4;
		return v < 100_000 ? 5 : v < 1_000_000 ? 6 : v < 10_000_000 ? 7 : 8;
	}

	/** 64 bits hauts du produit non signé. */
	private static long multiplieHaut(final long a, final long b) {
		return Math.multiplyHigh(a, b) + (a >> 63 & b) + (b >> 63 & a);
	}

	/**
	 * @return la valeur de b[debut, fin[, identique à Double.parseDouble, ou {@link #INVALIDE} si l'écriture ou la
	 *         valeur sortent des cas traités.
	 */
	public static double lit(final byte[] b, final int debut, final int fin) {
		int i = debut;
		final boolean negatif = i < fin && b[i] == '-';
		if (negatif)
			i++;
		long w = 0; // non signé
		int chiffres = 0;
		int q = 0;
		final int debutEntier = i;
		for (; i < fin; i++) {
			final int d = b[i] - '0';
			if (d < 0 || d > 9)
				break;
			if (w == 0 && d == 0)
				continue; // zéro de tête
			if (++chiffres > 19)
				return INVALIDE;
			w = w * 10 + d;
		}
		if (i == debutEntier)
			return INVALIDE;
		if (i < fin && b[i] == '.') {
			final int debutDecimales = ++i;
			// décimales par blocs de 8 chiffres (SWAR), même effet que chiffre par chiffre
			while (i + 8 <= fin) {
				final long x = (long) HUIT_OCTETS.get(b, i);
				if (((x & 0xF0F0F0F0F0F0F0F0L) | ((x + 0x0606060606060606L) & 0xF0F0F0F0F0F0F0F0L) >>> 4)
						!= 0x3333333333333333L)
					break;
				final int v = huitChiffres(x);
				if (w == 0) {
					// zéros de tête non comptés
					if (v != 0) {
						chiffres = nbChiffres(v);
						w = v;
					}
				} else {
					chiffres += 8;
					if (chiffres > 19)
						return INVALIDE;
					w = w * 100_000_000L + v;
				}
				q -= 8;
				i += 8;
			}
			for (; i < fin; i++) {
				final int d = b[i] - '0';
				if (d < 0 || d > 9)
					break;
				q--;
				if (w == 0 && d == 0)
					continue;
				if (++chiffres > 19)
					return INVALIDE;
				w = w * 10 + d;
			}
			if (i == debutDecimales)
				return INVALIDE;
		}
		if (i < fin && (b[i] == 'e' || b[i] == 'E')) {
			i++;
			boolean exposantNegatif = false;
			if (i < fin && (b[i] == '-' || b[i] == '+'))
				exposantNegatif = b[i++] == '-';
			final int debutExposant = i;
			int e = 0;
			for (; i < fin; i++) {
				final int d = b[i] - '0';
				if (d < 0 || d > 9 || i - debutExposant >= 4)
					return INVALIDE;
				e = e * 10 + d;
			}
			if (i == debutExposant)
				return INVALIDE;
			q += exposantNegatif ? -e : e;
		}
		if (i != fin)
			return INVALIDE;
		return valeur(negatif, w, q);
	}

	/** @return (-1)^negatif * w * 10^q arrondi, w non signé, ou {@link #INVALIDE}. */
	static double valeur(final boolean negatif, final long w, final int q) {
		if (w == 0)
			return negatif ? -0.0 : 0.0;
		if (q >= -22 && q <= 22 && w >= 0 && w <= 1L << 53) {
			final double d = q < 0 ? w / PUISSANCES[-q] : w * PUISSANCES[q];
			return negatif ? -d : d;
		}
		if (q < Q_MIN || q > Q_MAX)
			return INVALIDE;
		final int lz = Long.numberOfLeadingZeros(w);
		final long m = w << lz;
		final long th = Tables.HAUT[q - Q_MIN];
		final long tl = Tables.BAS[q - Q_MIN];
		// P = m * T sur 192 bits : haut, milieu, (bas ignoré : seule sa retenue compte)
		final long p1l = m * th;
		final long p2h = multiplieHaut(m, tl);
		final long milieu = p1l + p2h;
		final long haut = multiplieHaut(m, th) + (Long.compareUnsigned(milieu, p1l) < 0 ? 1 : 0);
		// près de 2^191, le bit de tête du produit exact peut différer
		if (haut == Long.MAX_VALUE && milieu == -1 || haut == Long.MIN_VALUE && milieu == 0)
			return INVALIDE;
		final int bitTete = (int) (haut >>> 63); // produit dans [2^190, 2^192[
		final int garde = 10 + bitTete; // bits de haut sous les 53 bits de la mantisse
		final long reste = haut & (1L << garde) - 1;
		final long moitie = 1L << garde - 1;
		// P à moins de 2^64 d'un milieu entre deux doubles : arrondi incertain
		if (reste == moitie && milieu == 0 || reste == moitie - 1 && milieu == -1)
			return INVALIDE;
		long mantisse = haut >>> garde;
		int exposant = 190 + bitTete + q - Tables.DECALAGE[q - Q_MIN] - lz;
		if (reste >= moitie)
			mantisse++;
		if (mantisse == 1L << 53) {
			mantisse = 1L << 52;
			exposant++;
		}
		final int biaise = exposant + 1023;
		if (biaise < 1 || biaise > 2046)
			return INVALIDE;
		final long bits = mantisse & (1L << 52) - 1 | (long) biaise << 52 | (negatif ? 1L << 63 : 0);
		return Double.longBitsToDouble(bits);
	}
}
