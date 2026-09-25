package io.github.giraudsa.fidelis.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Copie privée d'un format de date de la configuration (SimpleDateFormat n'est
 * pas thread-safe). Équivalent de {@code new SimpleDateFormat(pattern)} suivi de
 * {@code setTimeZone}, sans reparser le motif à chaque sérialisation : on clone
 * un prototype mis en cache par motif et fuseau horaire.
 */
public final class CopieFormatDate {
	private static final Map<String, SimpleDateFormat> prototypes = new ConcurrentHashMap<>();

	public static DateFormat copie(final SimpleDateFormat dateFormat) {
		final String motif = dateFormat.toPattern();
		final String fuseau = dateFormat.getTimeZone().getID();
		final SimpleDateFormat prototype = prototypes.computeIfAbsent(motif + '\u0000' + fuseau, k -> {
			final SimpleDateFormat nouveau = new SimpleDateFormat(motif);
			nouveau.setTimeZone(dateFormat.getTimeZone());
			return nouveau;
		});
		return (DateFormat) prototype.clone();
	}

	/** par motif et fuseau : le format copié est-il le format ISO UTC par défaut ? */
	private static final Map<String, Boolean> isoUtc = new ConcurrentHashMap<>();

	/** @return DatesIso.estMotifIsoUtc(copie(dateFormat)), sans faire la copie. */
	public static boolean estIsoUtc(final SimpleDateFormat dateFormat) {
		final String motif = dateFormat.toPattern();
		final String fuseau = dateFormat.getTimeZone().getID();
		return isoUtc.computeIfAbsent(motif + '\u0000' + fuseau,
				k -> io.github.giraudsa.fidelis.utils.io.DatesIso.estMotifIsoUtc(copie(dateFormat)));
	}

	private CopieFormatDate() {
	}
}
