package io.github.giraudsa.fidelis.utils.champ;

/**
 * Écrit tous les champs d'un objet d'une classe donnée, dans l'ordre d'un tableau de champs : pour chaque champ i,
 * appelle sur le contexte ecritXxx(valeur, champs[i]) (Xxx : type primitif, ou Objet). Implémentation générée en
 * mémoire ({@link GenerateurSerialiseurs}).
 */
public interface EcrivainChamps {
	void ecrit(Object objet, Object contexte, Champ[] champs) throws Exception; // NOSONAR : relaie les exceptions du contexte
}
