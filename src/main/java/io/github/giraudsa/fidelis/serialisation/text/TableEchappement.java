package io.github.giraudsa.fidelis.serialisation.text;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Map.Entry;

import io.github.giraudsa.fidelis.utils.io.SortieTexte;

/**
 * Table de remplacement des caractères à échapper, indexée directement par le
 * caractère (pas de boxing ni de hachage), et écriture par tronçons des parties
 * qui n'ont rien à échapper.
 */
public final class TableEchappement {
	private final String[] remplacements;

	public TableEchappement(final Map<Character, String> remplacementChars) {
		int max = 0;
		for (final Character c : remplacementChars.keySet())
			max = Math.max(max, c);
		remplacements = new String[max + 1];
		for (final Entry<Character, String> e : remplacementChars.entrySet())
			remplacements[e.getKey()] = e.getValue();
	}

	/** @return la table des remplacements, indexée par caractère (partagée : ne pas modifier). */
	public String[] remplacements() {
		return remplacements;
	}

	public void ecris(final Writer writer, final String aEchapper) throws IOException {
		if (writer instanceof SortieTexte) { // cas courant : directement dans le tampon
			((SortieTexte) writer).writeEchappe(aEchapper, remplacements);
			return;
		}
		final int longueur = aEchapper.length();
		int debut = 0;
		for (int i = 0; i < longueur; i++) {
			final char c = aEchapper.charAt(i);
			if (c < remplacements.length) {
				final String remplacement = remplacements[c];
				if (remplacement != null) {
					if (i > debut)
						writer.write(aEchapper, debut, i - debut);
					writer.write(remplacement);
					debut = i + 1;
				}
			}
		}
		if (debut < longueur)
			writer.write(aEchapper, debut, longueur - debut);
	}
}
