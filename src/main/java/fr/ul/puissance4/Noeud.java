package fr.ul.puissance4;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class Noeud {
    /**
     * La constante <code>c</code> de l'algorithme MCTS, utilisée en tant que coefficient d'exploration.
     * Un coefficient <code>c</code> trop grand augmente les chances de jouer aléatoirement, tandis qu'un
     * coefficient trop petit empêche de voir de meilleurs coups.
     * <p>
     * Par défaut égal à <code>Math.sqrt(2)</code>.
     **/
    private static final double C = Math.sqrt(2);

    /**
     * Le joueur qui a joué pour arriver à ce noeud.
     */
    private int joueur; //joueur aui a joué pour arrivé ici
    /**
     * Le coup joué par le {@link #joueur} pour arriver à ce noeud.
     */
    private Coup coup; // coup joué par ce joueur pour arriver ici
    /**
     * L'état global du puissance 4 dans ce noeud (après application du {@link #coup}.
     */
    private Etat etat; //etat du jeu
    /**
     * Le noeud parent de ce noeud.
     * <p>
     * Vaut <code>null</code> si le noeud est la racine de l'arbre.
     */
    private Noeud parent;
    /**
     * La liste des noeuds enfants de ce noeud.
     * <p>
     * Vide si le noeud est une feuille.
     */
    private final List<Noeud> enfants; // Liste d'enfants : chaque enfant correspond à un coup possible
    //Pour MCTS
    /**
     * Le nombre de victoires pour le joueur courant qui ont eu lieu en dessous de ce noeud.
     */
    private int nb_victoires;
    /**
     * Le nombre de simulations qui sont passées à travers ce noeud.
     */
    private int nb_simus;
    /**
     * La valeur de UCB1 pour le noeud actuel (afin d'éviter de le recalculer tout le temps).
     *
     * @see #calculerUCB1()
     */
    private double ucb1;
    /**
     * <code>true</code> si tous les noeuds fils ont été explorés.
     * <p>
     * Permet de spécifier à l'algorithme MCTS qu'il n'est plus utile d'aller plus bas dans le noeud.
     */
    private boolean terminal;

    public Noeud() {
        this.joueur = 0;
        this.coup = new Coup(0);
        etat = new Etat();
        parent = null;
        enfants = new ArrayList<>();
        nb_victoires = 0;
        nb_simus = 0;
        ucb1 = 0.d;
        terminal = false;
    }

    /**
     * Crée un nouveau noeud, fils du noeud parent à partir duquel on a joué un coup spécifique.
     *
     * @param parent le noeud parent duquel on joue
     * @param coup   le coup joué pour arriver à ce noeud
     */
    public Noeud(Noeud parent, Coup coup) {
        this();

        if (parent != null && coup != null) {
            // on copie l'état du parent et on joue le coup donné
            this.etat = parent.etat.copieEtat();
            this.etat.jouerCoup(coup);

            this.coup = coup;

            // on s'ajoute aux enfants de notre parent
            parent.enfants.add(this);

            // on prend l'autre joueur par rapport au parent
            this.joueur = parent.joueur;
            this.changerJoueur();
        } else {
            this.etat = null;
            this.coup = null;
            this.joueur = 0;
        }
        this.parent = parent;

        //Pour MCTS
        this.nb_simus = this.nb_victoires = 0;
        this.ucb1 = 0.d;
    }

    public int getNb_victoires() {
        return nb_victoires;
    }

    public int getNb_simus() {
        return nb_simus;
    }

    public Coup getCoup() {
        return coup;
    }

    public Etat getEtat() {
        return etat;
    }

    public void setEtat(Etat etat) {
        this.etat = etat;
    }

    public Noeud getParent() {
        return parent;
    }

    public List<Noeud> getEnfants() {
        return enfants;
    }

    public Noeud getEnfants(Coup coup) {
        for (Noeud c : enfants){
            if(c.getCoup() == coup)
                return c;
        }
        return new Noeud();
    }

    /**
     * Rend le noeud terminal, c'est-à-dire qu'il n'est pas nécessaire de visiter ses enfants.
     * @param terminal la valeur de l'indicateur
     */
    public void setTerminal(boolean terminal) {
        this.terminal = terminal;
    }

    /**
     * Vérifie si un noeud est une feuille (c'est-à-dire qu'il n'a pas d'enfants).
     *
     * @return <code>true</code> si le noeud n'a pas d'enfants, <code>false</code> sinon
     */
    public boolean estFeuille() {
        return this.enfants.isEmpty();
    }

    /**
     * Vérifie si le noeud est terminal.
     * @return <code>true</code> si le noeud n'a plus besoin d'être exploré, <code>false</code> sinon
     */
    public boolean estTerminal() {
        return this.terminal;
    }

    /**
     * Change le joueur jouant dans le noeud courant.
     */
    public void changerJoueur() {
        this.joueur = 1 - this.joueur;
    }

    /**
     * Sélectionne le meilleur coup possible en partant du noeud actuel.
     *
     * @return le meilleur coup possible qui descend depuis ce noeud
     */
    public Coup selection() {
        Coup best = null;
        double val = Double.NEGATIVE_INFINITY;

        for (Coup c : this.etat.coupsPossibles()) {
            Optional<Noeud> n = this.enfants.stream().filter(node -> node.coup.equals(c)).findFirst();
            if (n.isEmpty()) {
                // chemin non exploré : on explore
                return c;
            }
            Noeud nd = n.get();
            if (!nd.estTerminal()) {
                // noeud non terminal mais déjà (partiellement) exploré
                double ucb1 = nd.calculerUCB1();
                if (ucb1 > val) {
                    best = c;
                    val = ucb1;
                }
            }
        }

        return best;
    }

    /**
     * Récupère l'enfant atteint par le coup donné depuis la racine locale <code>this</code>.
     *
     * @param c le coup menant à l'enfant
     * @return le noeud enfant s'il existe, sinon <code>null</code>
     */
    public Noeud enfantAvecCoup(Coup c) {
        return this.enfants.stream().filter(n -> n.coup.equals(c)).findFirst().orElse(null);
    }

    /**
     * MCTS : développe le noeud en ajoutant tous les coups possibles en dessous.
     * @return le noeud développé à partir duquel faire tourner la simulation
     */
    public Noeud developpement() {
        if (this.estTerminal()) // si on est terminal, il y a probablement rien à développer
            return this;

        List<Coup> possibles = this.etat.coupsPossibles();
        if (possibles.isEmpty())
            return this;
        Coup c = possibles.get(new Random().nextInt(possibles.size()));
        if (c == null)
            return this; // fin de la partie

        return new Noeud(this, c);
    }

    /**
     * Retourne le ratio de victoires calculé par la formule
     * <pre>{@code
     *        ⎧
     *        ⎪    w(i)
     * R(i) = ⎨  ────────       si N(i) ≠ 0
     *        ⎪    N(i)
     *        ⎪
     *        ⎪
     *        ⎪  + ∞            sinon
     *        ⎩
     * }</pre>
     *
     * @return la valeur de <code>R(i)</code> pour le noeud actuel (<code>this</code>).
     */
    public double ratio() {
        if (nb_simus != 0)
            return (double) nb_victoires / (double) nb_simus;
        else
            return Double.POSITIVE_INFINITY;
    }

    /**
     * Propage les scores obtenus le long de l'arbre, en partant de la feuille, jusqu'à la racine.
     */
    public void propagationScore(FinDePartie score) {
        Noeud ptr = this;

        ptr.nb_simus += 1;
        if (ptr.joueur == Etat.COMPUTER_PLAYER && score == FinDePartie.ORDI_GAGNE) ptr.nb_victoires += 1;
        ptr.calculerUCB1();

        while (ptr.parent != null) {
            ptr = ptr.parent;

            ptr.nb_simus += 1;
            if (ptr.joueur == Etat.COMPUTER_PLAYER && score == FinDePartie.ORDI_GAGNE) ptr.nb_victoires += 1;
            ptr.calculerUCB1();
        }
    }

    /**
     * Calcule la valeur de <code>B(i)</code> selon la formule
     * <pre>{@code
     *        ⎧
     *        ⎪                         ,──────────────────────
     *        ⎪      w(i)              /   ln N(parent(i))
     * B(i) = ⎨   ± ────── + c × \    /  ────────────────────          si N(i) ≠ 0 ∧ parent(i) existe
     *        ⎪      N(i)         \  /          N(i)
     *        ⎪                    \/
     *        ⎪
     *        ⎪
     *        ⎪   + ∞                                                  sinon
     *        ⎩
     * }</pre>
     *
     * @return La valeur de <code>B(i)</code> pour le noeud courant (<code>this</code>).
     * @see Noeud#C
     */
    private double calculerUCB1() {
        if (nb_simus != 0 && this.parent != null) {
            ucb1 = ((double) nb_victoires / (double) nb_simus) + C * Math.sqrt(Math.log(this.parent.nb_simus) / nb_simus);
            ucb1 = this.joueur == Etat.COMPUTER_PLAYER ? +ucb1 : -ucb1;
        } else
            ucb1 = Double.POSITIVE_INFINITY;

        return ucb1;
    }
}
