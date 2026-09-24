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

	/** renvoyé par {@link #lit} quand la chaîne n'est pas une date canonique de ce format dans la plage. */
	public static final long INVALIDE = Long.MIN_VALUE;

	/**
	 * Lit une date canonique de ce format (24 caractères, champs valides, années 1583 à 9999) : même résultat que
	 * SimpleDateFormat.parse pour ces chaînes. @return INVALIDE sinon (l'appelant garde SimpleDateFormat).
	 */
	public static long lit(final String s) {
		if (s.length() != LONGUEUR || s.charAt(4) != '-' || s.charAt(7) != '-' || s.charAt(10) != 'T'
				|| s.charAt(13) != ':' || s.charAt(16) != ':' || s.charAt(19) != '.' || s.charAt(23) != 'Z')
			return INVALIDE;
		final int annee = nombre(s, 0, 4);
		final int mois = nombre(s, 5, 2);
		final int jour = nombre(s, 8, 2);
		final int heure = nombre(s, 11, 2);
		final int minute = nombre(s, 14, 2);
		final int seconde = nombre(s, 17, 2);
		final int milli = nombre(s, 20, 3);
		if (annee < 1583 || mois < 1 || mois > 12 || jour < 1 || jour > joursDuMois(annee, mois) || heure > 23
				|| minute > 59 || seconde > 59 || milli < 0)
			return INVALIDE; // champ absent (-1), hors bornes : SimpleDateFormat (lenient) décide
		// année, mois, jour -> jours depuis 1970-01-01 (algorithme « days_from_civil » de H. Hinnant)
		final long y = annee - (mois <= 2 ? 1 : 0);
		final long ere = Math.floorDiv(y, 400L);
		final long anneeEre = y - ere * 400;
		final long jourAnnee = (153L * (mois > 2 ? mois - 3 : mois + 9) + 2) / 5 + jour - 1;
		final long jourEre = anneeEre * 365 + anneeEre / 4 - anneeEre / 100 + jourAnnee;
		final long jours = ere * 146_097L + jourEre - 719_468L;
		return ((jours * 24 + heure) * 60 + minute) * 60_000L + seconde * 1000L + milli;
	}

	/** @return la valeur des n chiffres à partir de debut, ou -1 si l'un n'est pas un chiffre ASCII. */
	private static int nombre(final String s, final int debut, final int n) {
		int v = 0;
		for (int i = debut; i < debut + n; i++) {
			final char c = s.charAt(i);
			if (c < '0' || c > '9')
				return -1;
			v = v * 10 + c - '0';
		}
		return v;
	}

	private static int joursDuMois(final int annee, final int mois) {
		switch (mois) {
		case 2:
			return annee % 4 == 0 && (annee % 100 != 0 || annee % 400 == 0) ? 29 : 28;
		case 4:
		case 6:
		case 9:
		case 11:
			return 30;
		default:
			return 31;
		}
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
