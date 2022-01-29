package fr.ul.puissance4;

import java.util.ArrayList;
import java.util.List;

public class Etat {
    public static final int LIGNE = 6;
    public static final int COLONNE = 7;
    public static final int HUMAN_PLAYER = 0;
    public static final int COMPUTER_PLAYER = 1;
    private static final int STRAT_MAX = 0;
    private static final int STRAT_ROBUSTE = 1;
    /**
     * L'état actuel de la grille du puissance 4.
     * <p>
     * Taille : {@link #LIGNE} × {@link #COLONNE}
     */
    private final char[][] plateau; //ligne/colonne
    /**
     * Undocumented
     */
    private final int strategy = STRAT_MAX;
    /**
     * Contient le joueur actuellement en train de jouer.
     * Peut être soit {@link #HUMAN_PLAYER} soit {@link #COMPUTER_PLAYER}.
     */
    private int joueur; //à qui de jouer

    /**
     * Crée un état vide dans lequel l'ordinateur commence.
     */
    public Etat() {
        joueur = COMPUTER_PLAYER; //L'ordinateur qui commence :)
        plateau = new char[LIGNE][COLONNE]; //plateau du Puissance 4

        //Initialisation du plateau vide
        int i, j;
        for (i = 0; i < LIGNE; i++) {
            for (j = 0; j < COLONNE; j++) {
                this.plateau[i][j] = ' ';
            }
        }
    }

    /**
     * Crée un état vide dans lequel le joueur donné commence.
     *
     * @param joueur le joueur qui commence
     */
    public Etat(int joueur) {
        this();

        this.joueur = joueur;
    }

    /**
     * Crée un nouvelle état à partir d'un joueur et d'un plateau.
     *
     * @param j   le joueur dans l'état (soit {@link #HUMAN_PLAYER} soit {@link #COMPUTER_PLAYER})
     * @param tab le plateau de taille {@link #LIGNE} × {@link #COLONNE}
     */
    private Etat(int j, char[][] tab) {
        this.joueur = j;
        this.plateau = tab;
    }

    /**
     * Get du plateau
     *
     * @return le plateau de jeu
     */
    public char[][] getPlateau() {
        return plateau;
    }

    /**
     * Get le joueur
     *
     * @return Quel joueur joue
     */
    public int getJoueur() {
        return joueur;
    }

    /**
     * Set joueur
     *
     * @param joueur le joueur qui va jouer
     */
    public void setJoueur(int joueur) {
        this.joueur = joueur;
    }

    /**
     * Copie de l'Etat en cours
     *
     * @return un nouvel état identique à <code>this</code> mais dont la modification n'affectera pas <code>this</code>.
     */
    public Etat copieEtat() {
        char[][] tab = new char[LIGNE][COLONNE];
        for (int i = 0; i < LIGNE; i++) {
            System.arraycopy(plateau[i], 0, tab[i], 0, COLONNE);
        }
        int jour = joueur;
        return new Etat(jour, tab);
    }

    /**
     * Récupère tous les coups possibles dans la grille.
     *
     * @return la liste contenant tous les coups possibles (de taille ≤ 9)
     */
    public List<Coup> coupsPossibles() {
        List<Coup> listCoup = new ArrayList<>();
        int i, j;
        for (i = 0; i < LIGNE; i++) {
            for (j = 0; j < COLONNE; j++) {
                if (plateau[i][j] == ' ')
                    listCoup.add(new Coup(j));
            }
        }
        return listCoup;

    }

    /**
     * Change le joueur qui joue dans l'état actuel.
     */
    public void changerJoueur() {
        this.joueur = 1 - this.joueur;
    }

    /**
     * Pose le pion à la colonne indiquée par le coup, si celui-ci peut être placé (si on ne déborde pas du plateau).
     *
     * @param coup le numéro de colonne où on souhaite poser le pion
     * @return <code>true</code> si le pion a été joué, <code>false</code> sinon.
     */
    public boolean jouerCoup(Coup coup) {
        if (coup == null)
            return false;

        //verifie que la ligne n'est pas pleine
        if (this.getPlateau()[0][coup.getColonne()] != ' ')
            return false;

        this.getPlateau()[0][coup.getColonne()] = joueur == HUMAN_PLAYER ? 'O' : 'X';
        for (int i = 1; i < LIGNE; i++) {
            if (this.getPlateau()[i][coup.getColonne()] == ' ') {
                this.getPlateau()[i][coup.getColonne()] = joueur == HUMAN_PLAYER ? 'O' : 'X';
                this.getPlateau()[i - 1][coup.getColonne()] = ' ';
            }
        }

        this.changerJoueur();
        return true;
    }

    /**
     * Teste si l'état correspond à une partie finie.
     * <p>
     * Une partie est finie si : <ol>
     * <li>Soit toutes les cases sont occupées et il n'y a pas de gagnant</li>
     * <li>Le joueur <code>O</code> a réussi à aligner 4 de ses pions</li>
     * <li>Le joueur <code>X</code> a aligné 4 de ses pions</li>
     * <li>Aucune des 3 possibilités avant (partie non finie)</li>
     * </ol>
     *
     * @return <ol>
     * <li>{@link FinDePartie#MATCH_NUL}</li>
     * <li>{@link FinDePartie#HUMAIN_GAGNE}</li>
     * <li>{@link FinDePartie#ORDI_GAGNE}</li>
     * <li>{@link FinDePartie#NON}</li>
     * </ol>
     */
    public FinDePartie testFin() {
        int k, n = 0;
        for (int i = 0; i < LIGNE; i++) {
            for (int j = 0; j < COLONNE; j++) {
                if (plateau[i][j] != ' ') {
                    n++; //nb de coups joués

                    //lignes
                    k = 0;
                    while (k < 4 && i + k < LIGNE && plateau[i + k][j] == plateau[i][j])
                        k++;
                    if (k == 4) return plateau[i][j] == 'X' ? FinDePartie.ORDI_GAGNE : FinDePartie.HUMAIN_GAGNE;

                    // colonnes
                    k = 0;
                    while (k < 4 && j + k < COLONNE && plateau[i][j + k] == plateau[i][j])
                        k++;
                    if (k == 4)
                        return plateau[i][j] == 'X' ? FinDePartie.ORDI_GAGNE : FinDePartie.HUMAIN_GAGNE;

                    // diagonales
                    k = 0;
                    while (k < 4 && i + k < LIGNE && j + k < COLONNE && plateau[i + k][j + k] == plateau[i][j])
                        k++;
                    if (k == 4)
                        return plateau[i][j] == 'X' ? FinDePartie.ORDI_GAGNE : FinDePartie.HUMAIN_GAGNE;

                    k = 0;
                    while (k < 4 && i + k < LIGNE && j - k >= 0 && plateau[i + k][j - k] == plateau[i][j])
                        k++;
                    if (k == 4)
                        return plateau[i][j] == 'X' ? FinDePartie.ORDI_GAGNE : FinDePartie.HUMAIN_GAGNE;

                }
            }
        }
        if (n == COLONNE * LIGNE) return FinDePartie.MATCH_NUL;
        return FinDePartie.NON;
    }

    /**
     * Fait jouer l'ordinateur sur l'état actuel à l'aide de l'algorithme MCTS UCT.
     *
     * @param tempsmax le temps maximum en ms durant lequel l'algorithme a le droit de s'exécuter
     */
    public void ordijoue_mcts(long tempsmax) {
        final long tic = System.currentTimeMillis();

        //Créer l'arbre de recherche
        Noeud racine = new Noeud(null, null);
        racine.setEtat(copieEtat());

        Thread progress = new Thread(() -> {
            final String indicators = "⠋⠙⠹⠸⠼⠴⠦⠧⠇⠏";
            int i = 0;

            while (true) {
                System.out.print("\r" + indicators.charAt(i) + " L'ordinateur réfléchit... (" + ((tempsmax - (System.currentTimeMillis() - tic)) / 1000) + "s restantes)");
                System.out.flush();

                try {
                    Thread.sleep(75);
                } catch (InterruptedException e) {
                    // do nothing
                    break;
                }

                i = ++i > indicators.length() - 1 ? 0 : i;
            }

            System.out.println();
        });

        progress.start();
        do {
            // MCTS :
            // - sélection du noeud le + propice à être développé
            // - développement du noeud
            // - simulation aléatoire d'une partie à partir de ce noeud
            // - propagation du score jusqu'à la racine de l'arbre

            Noeud toExpand = selection(racine);
            if (toExpand == null)
                break; // l'arbre a été complètement exploré, plus aucun noeud n'est sélectionnable

            Noeud expanded = toExpand.developpement();
            FinDePartie status = simulation(expanded);
            expanded.propagationScore(status);


            // on applique MCTS jusqu'à avoir dépassé le temps limite
        } while (System.currentTimeMillis() - tic < tempsmax);
        progress.interrupt();

        System.out.println("\n Le nombre de simulation réalisée : " + racine.getNb_simus());


        //Vérifier que le joueur ne va pas gagner au prochain tour !
        Noeud racineE = new Noeud(null, null);
        racineE.setEtat(copieEtat());

        List<Coup> coups = coupsPossibles();
        Noeud enfant;
        Coup meilleur_coup = null;
        racineE.getEtat().setJoueur(HUMAN_PLAYER);

        Noeud e1 = null;
        for (Coup coup : coups) {
            enfant = new Noeud(racineE, coup);

            FinDePartie fin = enfant.getEtat().testFin();
            if (fin == FinDePartie.HUMAIN_GAGNE) {
                meilleur_coup = coup;
                e1 = enfant;
            }
        }
        //Si le joueur peut gagner
        if (meilleur_coup != null) {
            //Vérifier si l'ordi peut gagner (PRIORITAIRE)
            for (Noeud enf : racine.getEnfants()) {
                if (enf.getEtat().testFin() == FinDePartie.ORDI_GAGNE) {
                    meilleur_coup = enf.getCoup();
                    e1 = enf;
                }
            }
            System.out.println("\n La probabilité de victoire est : " + e1.getNb_victoires() + " / " + e1.getNb_simus() + " = " + e1.ratio());
            jouerCoup(meilleur_coup);
        } else {
            Coup best = null;
            double val = Double.NEGATIVE_INFINITY;
            // on récupère le meilleur coup à jouer (celui qui mène au + grand nombre de parties gagnées)
            Noeud e = null;
            for (Noeud enf : racine.getEnfants()) {
                @SuppressWarnings("ConstantConditions") double val2 = strategy == STRAT_MAX ? enf.ratio() : (double) enf.getNb_simus();
                if (val2 > val) {
                    val = val2;
                    best = enf.getCoup();
                    e = enf;
                }
            } // et on joue ce coup

            assert e != null;
            System.out.println("\n La probabilité de victoire est : " + e.getNb_victoires() + " / " + e.getNb_simus() + " = " + e.ratio());
            jouerCoup(best);
        }


    }

    /**
     * MCTS : sélection du noeud à développer.
     *
     * @param racine la racine de l'arbre à partir de laquelle chercher le noeud
     * @return <code>null</code> si aucun noeud n'est à développer, sinon le noeud à développer
     */
    private Noeud selection(Noeud racine) {
        Noeud current = racine, next;

        do {
            Coup c = current.selection();

            if (c == null) {
                // feuille car pas de coups possibles
                current.setTerminal(true);
                if (current == racine) {
                    return null; // arbre fini d'être exploré
                } else {
                    // on revient au parent, peut-être qu'il a encore des enfants à explorer
                    next = current.getParent();
                }
            } else {
                next = current.enfantAvecCoup(c);
                if (next == null) {
                    // jamais exploré ici...
                    next = new Noeud(current, c);
                }
            }

            current = next;
        } while (!current.estFeuille() && !current.estTerminal());

        return current;
    }

    /**
     * MCTS : simule une partie aléatoirement à partir d'un noeud
     *
     * @param racineLocale le noeud servant de racine locale à la simulation aléatoire
     * @return si la partie est finie ou non
     */
    private FinDePartie simulation(Noeud racineLocale) {
        Noeud current = racineLocale;
        FinDePartie fin = current.estTerminal() ? (current.getNb_victoires() == 1 ? FinDePartie.ORDI_GAGNE : FinDePartie.HUMAIN_GAGNE) : FinDePartie.NON;

        while (!current.estTerminal() && (fin = current.getEtat().testFin()) == FinDePartie.NON) {
            // tant que la partie n'est pas finie, on continue de simuler aléatoirement
            current = current.developpement();
        }
        current.setTerminal(true);

        // et on donne le score final (match nul, gagnant ou perdant)
        return fin;
    }

    /**
     * Affichage du plateau de jeu
     *
     * @return le plateau actuel
     */
    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        int i, j;
        string.append("   ┃");
        for (j = 1; j <= COLONNE; j++)
            string.append(" ").append(j).append(" ┃");
        string.append("\n").append("━━━╋━━━╇━━━╇━━━╇━━━╇━━━╇━━━╇━━━┫").append("\n");

        for (i = 1; i <= LIGNE; i++) {
            string.append(" ").append(i).append(" ┃");
            for (j = 0; j < COLONNE; j++) {
                string.append(" ");
                char x = plateau[i - 1][j];
                string.append(x == 'X' ? "\033[41m\033[31mX" : x == 'O' ? "\033[43m\033[33mO" : " ").append("\033[0m");
                string.append(j == COLONNE - 1 ? " ┃" : " │");
            }
            string.append("\n").append(i == LIGNE ? "━━━┻━━━┷━━━┷━━━┷━━━┷━━━┷━━━┷━━━┛" : "━━━╉───┼───┼───┼───┼───┼───┼───┨").append("\n");
        }
        return string.toString();
    }
}
