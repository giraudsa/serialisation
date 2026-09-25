package io.github.giraudsa.fidelis.utils.champ;

/**
 * Déduplication adaptative des chaînes d'un champ : après {@link #ECHANTILLON} chaînes, si moins de 5 % ont été
 * retrouvées (identifiants, adresses...), la recherche dans la table ne vaut pas son coût et elle est abandonnée pour
 * ce champ. Les compteurs ne sont écrits que pendant l'échantillon (courses bénignes : ce n'est qu'une heuristique).
 */
final class StatsDedoublonnage {
	private static final int ECHANTILLON = 256;
	private int recherches;
	private int trouvees;
	private boolean inutile;

	boolean isUtile() {
		return !inutile;
	}

	void note(final boolean trouvee) {
		if (recherches >= ECHANTILLON)
			return;
		if (trouvee)
			trouvees++;
		if (++recherches == ECHANTILLON)
			inutile = trouvees * 20 < ECHANTILLON;
	}
}
