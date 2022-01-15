package fr.ul.puissance4;

/**
 * Classe d'énumération d'état de fin de partie
 */
public enum FinDePartie {
    /**
     * Le jeu n'est pas encore fini.
     */
    NON,
    /**
     * Match nul : toutes les cases ont été remplies mais personne n'a gagné.
     */
    MATCH_NUL,
    /**
     * Le joueur ordinateur a gagné la partie.
     */
    ORDI_GAGNE,
    /**
     * Le joueur humain a gagné la partie.
     */
    HUMAIN_GAGNE
}
