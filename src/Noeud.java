import java.util.ArrayList;
import java.util.List;

public class Noeud {
    /**
     * La constante <code>c</code> de l'algorithme MCTS, utilisée en tant que coefficient d'exploration.
     * Un coefficient <code>c</code> trop grand augmente les chances de jouer aléatoirement, tandis qu'un
     * coefficient trop petit empêche de voir de meilleurs coups.
     * <p>
     * Par défaut égal à <code>Math.sqrt(2)</code>.
     **/
    private static final double C = Math.sqrt(2);

    private int joueur; //joueur aui a joué pour arrivé ici
    private Coup coup; // coup joué par ce joueur pour arriver ici
    private Etat etat; //etat du jeu
    private Noeud parent;
    private List<Noeud> enfant; // Liste d'enfants : chaque enfant correspond à un coup possible
    //Pour MCTS
    private int nb_victoires;
    private int nb_simus;
    private double ucb1;

    public Noeud() {
        this.joueur = 0;
        this.coup = new Coup(0);
        etat = new Etat();
        parent = null;
        enfant = new ArrayList<>();
        nb_victoires = 0;
        nb_simus = 0;
        ucb1 = 0.d;
    }

    public static Noeud nouveauNoeud(Noeud parent, Coup coup) {
        Noeud noeud = new Noeud();

        if (parent != null && coup != null) {
            noeud.etat = parent.etat.copieEtat();
            noeud.etat.jouerCoup(coup);
            noeud.setCoup(coup);
            noeud.changerJoueur();
        } else {
            noeud.setEtat(null);
            noeud.setCoup(null);
            noeud.setJoueur(0);
        }
        noeud.setParent(parent);

        //Pour mcts
        noeud.setNb_victoires(0);
        noeud.setNb_simus(0);

        return noeud;
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

    public List<Noeud> getEnfant() {
        return enfant;
    }

    public void setEnfant(List<Noeud> enfant) {
        this.enfant = enfant;
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

    public void setJoueur(int joueur) {
        this.joueur = joueur;
    }

    public void changerJoueur() {
        this.joueur = 1 - this.joueur;
    }

    public Noeud ajouterEnfant(Coup coup) {
        Noeud enfant = Noeud.nouveauNoeud(this, coup);
        this.enfant.add(enfant);
        return enfant;
    }

    /**
     * Propage les scores obtenus le long de l'arbre, en partant de la feuille, jusqu'à la racine.
     */
    public void propagationScore() {
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
     *        ⎪       w                /   ln N(parent(i))
     * B(i) = ⎨   ± ────── + c × \    /  ────────────────────          si N(i) ≠ 0
     *        ⎪      N(i)         \  /          N(i)
     *        ⎪                    \/
     *        ⎪
     *        ⎪
     *        ⎪     + ∞                                                sinon
     *        ⎩
     * </pre>
     *
     * @return La valeur de <code>B(i)</code> pour le noeud courant (<code>this</code>).
     * @see Noeud#C
     */
    private double calculerUCB1() {
        if (nb_simus != 0) {
            ucb1 = ((double) nb_victoires / (double) nb_simus) + C * Math.sqrt(Math.log(this.parent.nb_simus) / nb_simus);
            ucb1 = this.joueur == Etat.COMPUTER_PLAYER ? +ucb1 : -ucb1;
        } else
            ucb1 = Double.POSITIVE_INFINITY;

        return ucb1;
    }
}
