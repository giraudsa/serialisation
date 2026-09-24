package utils.io;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

/**
 * Formatage rapide, sans allocation, du format de date par défaut ("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" en UTC). Mêmes
 * caractères que SimpleDateFormat pour les années 1583 à 9999 (calendrier grégorien, années sur 4 chiffres) ; en
 * dehors, l'appelant garde SimpleDateFormat.
 */
public final class DatesIso {
	public static final String MOTIF = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	/** 1583-01-01T00:00:00.000Z et 9999-12-31T23:59:59.999Z. */
	public static final long MIN = -12_212_553_600_000L;
	public static final long MAX = 253_402_300_799_999L;
	/** nombre de caractères écrits. */
	public static final int LONGUEUR = 24;

	/** @return true si ce format produit exactement ce que {@link #ecris} écrit (pour MIN ≤ t ≤ MAX). */
	public static boolean estMotifIsoUtc(final DateFormat format) {
		if (!(format instanceof SimpleDateFormat))
			return false;
		final SimpleDateFormat f = (SimpleDateFormat) format;
		return MOTIF.equals(f.toPattern()) && "UTC".equals(f.getTimeZone().getID())
				&& f.getCalendar() instanceof GregorianCalendar && f.getNumberFormat() instanceof DecimalFormat
				&& ((DecimalFormat) f.getNumberFormat()).getDecimalFormatSymbols().getZeroDigit() == '0';
	}

	/** Écrit la date (MIN ≤ millis ≤ MAX) sur LONGUEUR caractères à partir de p. */
	public static void ecris(final long millis, final char[] b, final int p) {
		final long secondes = Math.floorDiv(millis, 1000L);
		final int milli = (int) Math.floorMod(millis, 1000L);
		final long jours = Math.floorDiv(secondes, 86_400L);
		final int seconde = (int) Math.floorMod(secondes, 86_400L);
		// jours depuis 1970-01-01 -> année, mois, jour (algorithme « civil_from_days » de H. Hinnant)
		final long z = jours + 719_468L;
		final long ere = Math.floorDiv(z, 146_097L);
		final long jourEre = z - ere * 146_097L;
		final long anneeEre = (jourEre - jourEre / 1460 + jourEre / 36_524 - jourEre / 146_096) / 365;
		final long jourAnnee = jourEre - (365 * anneeEre + anneeEre / 4 - anneeEre / 100);
		final long mp = (5 * jourAnnee + 2) / 153;
		final int jour = (int) (jourAnnee - (153 * mp + 2) / 5 + 1);
		final int mois = (int) (mp < 10 ? mp + 3 : mp - 9);
		final int annee = (int) (anneeEre + ere * 400 + (mois <= 2 ? 1 : 0));
		quatre(b, p, annee);
		b[p + 4] = '-';
		deux(b, p + 5, mois);
		b[p + 7] = '-';
		deux(b, p + 8, jour);
		b[p + 10] = 'T';
		deux(b, p + 11, seconde / 3600);
		b[p + 13] = ':';
		deux(b, p + 14, seconde / 60 % 60);
		b[p + 16] = ':';
		deux(b, p + 17, seconde % 60);
		b[p + 19] = '.';
		b[p + 20] = (char) ('0' + milli / 100);
		deux(b, p + 21, milli % 100);
		b[p + 23] = 'Z';
	}

	private static void deux(final char[] b, final int p, final int v) {
		b[p] = (char) ('0' + v / 10);
		b[p + 1] = (char) ('0' + v % 10);
	}

	private static void quatre(final char[] b, final int p, final int v) {
		deux(b, p, v / 100);
		deux(b, p + 2, v % 100);
	}

	private DatesIso() {
	}
}
