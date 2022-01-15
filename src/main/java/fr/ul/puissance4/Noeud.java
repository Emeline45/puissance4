package fr.ul.puissance4;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    private List<Noeud> enfants; // Liste d'enfants : chaque enfant correspond à un coup possible
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

    public Noeud() {
        this.joueur = 0;
        this.coup = new Coup(0);
        etat = new Etat();
        parent = null;
        enfants = new ArrayList<>();
        nb_victoires = 0;
        nb_simus = 0;
        ucb1 = 0.d;
    }

    /**
     * Crée un nouveau noeud, fils du noeud parent à partir duquel on a joué un coup spécifique.
     *
     * @param parent le noeud parent duquel on joue
     * @param coup le coup joué pour arriver à ce noeud
     */
    public Noeud(Noeud parent, Coup coup) {
        this();

        if (parent != null && coup != null) {
            // on copie l'état du parent et on joue le coup donné
            this.etat = parent.etat.copieEtat();
            this.etat.jouerCoup(coup);

            this.coup = coup;

            // on prend l'autre joueur par rapport au parent
            parent.changerJoueur();
            this.joueur = parent.joueur;
            parent.changerJoueur();
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

    public Coup getCoup() {
        return coup;
    }

    public void setCoup(Coup coup) {
        this.coup = coup;
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

    public void setParent(Noeud parent) {
        this.parent = parent;
    }

    public List<Noeud> getEnfants() {
        return enfants;
    }

    public void setEnfants(List<Noeud> enfants) {
        this.enfants = enfants;
    }

    public int getNb_victoires() {
        return nb_victoires;
    }

    public void setNb_victoires(int nb_victoires) {
        this.nb_victoires = nb_victoires;
    }

    public int getNb_simus() {
        return nb_simus;
    }

    public void setNb_simus(int nb_simus) {
        this.nb_simus = nb_simus;
    }

    public int getJoueur() {
        return joueur;
    }

    ////////////////////////////////////////////////////////////////////////:

    /**
     * Vérifie si un noeud est une feuille/terminal (c'est-à-dire qu'il n'a pas d'enfants).
     * @return <code>true</code> si le noeud n'a pas d'enfants, <code>false</code> sinon
     */
    public boolean estFeuille() {
        return this.enfants.isEmpty();
    }

    public void setJoueur(int joueur) {
        this.joueur = joueur;
    }

    public void changerJoueur() {
        this.joueur = 1 - this.joueur;
    }

    public Noeud ajouterEnfant(Coup coup) {
        Noeud enfant = new Noeud(this, coup);
        this.enfants.add(enfant);
        return enfant;
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
            if (!nd.estFeuille()) {
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
        // TODO: use `score` somewhere?
        Noeud ptr = this;

        ptr.nb_simus += 1;
        if (this.joueur == ptr.joueur) ptr.nb_victoires += 1;
        ptr.calculerUCB1();

        while (ptr.parent != null) {
            ptr = ptr.parent;

            ptr.nb_simus += 1;
            if (this.joueur == ptr.joueur) ptr.nb_victoires += 1;
            ptr.calculerUCB1();
        }
    }

    /**
     * Calcule la valeur de <code>B(i)</code> selon la formule
     * <pre>
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
     * </pre>
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
