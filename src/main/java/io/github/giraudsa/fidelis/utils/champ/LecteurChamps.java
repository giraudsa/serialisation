package io.github.giraudsa.fidelis.utils.champ;

/**
 * Lit les champs d'un objet d'une classe donnée, à partir d'un indice, dans l'ordre d'un tableau de champs : pour
 * chaque champ i, affecte au champ la valeur renvoyée par litXxx(champs[i]) sur le contexte. Implémentation générée
 * en mémoire ({@link GenerateurSerialiseurs}).
 */
public interface LecteurChamps {
	void lit(Object objet, Object contexte, Champ[] champs) throws Exception; // NOSONAR : relaie les exceptions du contexte
}
