import java.util.ArrayList;
import java.util.List;

public class Noeud {
    private int joueur; //joueur aui a joué pour arrivé ici
    private Coup coup; // coup joué par ce joueur pour arriver ici
    private Etat etat; //etat du jeu

    private Noeud parent;
    private List<Noeud> enfant; // Liste d'enfants : chaque enfant correspond à un coup possible

    //Pour MCTS
    private int nb_victoires;
    private int nb_simus;

    public Noeud(){
        this.joueur = 0;
        this.coup = new Coup(0);
        etat = new Etat();
        parent = null;
        enfant = new ArrayList<>();
        nb_victoires = 0;
        nb_simus = 0;
    }

    public void setCoup(Coup coup) {
        this.coup = coup;
    }

    public void setEtat(Etat etat) {
        this.etat = etat;
    }

    public void setParent(Noeud parent) {
        this.parent = parent;
    }

    public void setEnfant(List<Noeud> enfant) {
        this.enfant = enfant;
    }

    public void setNb_victoires(int nb_victoires) {
        this.nb_victoires = nb_victoires;
    }

    public void setNb_simus(int nb_simus) {
        this.nb_simus = nb_simus;
    }

    public Coup getCoup() {
        return coup;
    }

    public Etat getEtat() {
        return etat;
    }

    public Noeud getParent() {
        return parent;
    }

    public List<Noeud> getEnfant() {
        return enfant;
    }

    public int getNb_victoires() {
        return nb_victoires;
    }

    public int getNb_simus() {
        return nb_simus;
    }

    public int getJoueur() {
        return joueur;
    }

    public void setJoueur(int joueur) {
        this.joueur = joueur;
    }

    public void changerJoueur(){
        this.joueur = 1-this.joueur;
    }

    ////////////////////////////////////////////////////////////////////////:

    public static Noeud nouveauNoeud(Noeud parent, Coup coup){
        Noeud noeud = new Noeud();

        if(parent != null && coup != null){
            noeud.etat = parent.etat.copieEtat();
            noeud.etat.jouerCoup(coup);
            noeud.setCoup(coup);
            noeud.changerJoueur();
        }else {
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

    public Noeud ajouterEnfant(Coup coup){
        Noeud enfant = Noeud.nouveauNoeud(this, coup);
        this.enfant.add(enfant);
        return enfant;
    }
}
