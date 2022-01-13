import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Etat {
    public static final int LIGNE = 6;
    public static final int COLONNE = 7;

    private int joueur; //à qui de jouer
    private char plateau[][]; //ligne/colonne

    /**
     * Constructeur de l'Etat initial
     */
    public Etat() {
        joueur = 1; //L'ordinateur qui commence :)
        plateau = new char[LIGNE][COLONNE]; //plateau du Puissance 4

        //Initialisation du plateau vide
        int i,j;
        for(i=0; i < LIGNE; i++){
            for (j = 0; j <COLONNE; j++){
                this.plateau[i][j] = ' ';
            }
        }
    }

    public Etat(int j, char tab[][]) {
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
     * @return un Etat
     */
    public Etat copieEtat() {
        return new Etat(this.joueur, this.plateau);
    }

    public List<Coup> coupsPossibles(){
        List<Coup> listCoup = new ArrayList<>();
        int i,j;
        for(i=0; i < LIGNE; i++){
            for (j = 0; j <COLONNE; j++){
                if(plateau[i][j] == ' ')
                    listCoup.add(new Coup(j));
            }
        }
        return listCoup;

    }

    public void changerJoueur(){
        this.joueur = 1-this.joueur;
    }

    public boolean jouerCoup(Coup coup){
        //verifie que la ligne n'est pas pleine
        if(this.getPlateau()[0][coup.getColonne()] != ' ')
            return false;

        int i = Etat.LIGNE -1;
        while (this.getPlateau()[i--][coup.getColonne()] == ' ');
        this.getPlateau()[i][coup.getColonne()] = 'O';

        this.changerJoueur();
        return true;
    }

    public FinDePartie testFin(){
        return null;
    }

    /**
     * Affichage du plateau de jeu
     * @return le plateau actuel
     */
    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        int i,j;
        string.append("   ┃");
        for ( j = 1; j <= COLONNE; j++)
            string.append(" ").append(j).append(" ┃");
        string.append("\n").append("━━━╋━━━╇━━━╇━━━╇━━━╇━━━╇━━━╇━━━┫").append("\n");

        for(i=1; i <= LIGNE; i++) {
            string.append(" ").append(i).append(" ┃");
            for ( j = 0; j < COLONNE; j++)
                string.append(" ").append(plateau[i-1][j]).append(j==COLONNE-1 ? " ┃" : " │");
            string.append("\n").append(i==LIGNE ? "━━━┻━━━┷━━━┷━━━┷━━━┷━━━┷━━━┷━━━┛" : "━━━╉───┼───┼───┼───┼───┼───┼───┨").append("\n");
        }
        return string.toString();
    }
}
