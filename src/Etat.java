import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
        char tab[][] = new char[LIGNE][COLONNE];
        for(int i = 0; i< LIGNE; i++){
            for(int j = 0; j < COLONNE; j++){
                tab[i][j] = plateau[i][j];
            }
        }
        int jour = joueur;
        return new Etat(jour, tab);
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

        this.getPlateau()[0][coup.getColonne()] = joueur == 0 ? 'O' : 'X' ;
        for (int i = 1; i < LIGNE; i++){
            if(this.getPlateau()[i][coup.getColonne()] == ' ') {
                this.getPlateau()[i][coup.getColonne()] = joueur == 0 ? 'O' : 'X';
                this.getPlateau()[i-1][coup.getColonne()] = ' ' ;
            }
        }
        //while (i< Etat.LIGNE && this.getPlateau()[i++][coup.getColonne()] == ' ');
        //this.getPlateau()[i-1][coup.getColonne()] = joueur == 0 ? 'O' : 'X' ;

        this.changerJoueur();
        return true;
    }

    public FinDePartie testFin(){
        int k,n = 0;
        for(int i = 0; i < LIGNE; i++){
            for(int j = 0; j < COLONNE; j++){
                if (plateau[i][j] != ' ') {
                    n++; //nb de coups joués

                    //lignes
                    k=0;
                    while (k < 4 && i + k < LIGNE && plateau[i+k][j] == plateau[i][j])
                        k++;
                    if( k == 4) return plateau[i][j] == 'X' ? FinDePartie.ORDI_GAGNE : FinDePartie.HUMAIN_GAGNE;

                    // colonnes
                    k=0;
                    while ( k < 4 && j+k < COLONNE && plateau[i][j+k] == plateau[i][j] )
                        k++;
                    if ( k == 4 )
                        return plateau[i][j] == 'X'? FinDePartie.ORDI_GAGNE : FinDePartie.HUMAIN_GAGNE;

                    // diagonales
                    k=0;
                    while ( k < 4 && i+k < LIGNE && j+k < COLONNE && plateau[i+k][j+k] == plateau[i][j] )
                        k++;
                    if ( k == 4 )
                        return plateau[i][j] == 'X'? FinDePartie.ORDI_GAGNE : FinDePartie.HUMAIN_GAGNE;

                    k=0;
                    while ( k < 4 && i+k < LIGNE && j-k >= 0 && plateau[i+k][j-k] == plateau[i][j] )
                        k++;
                    if ( k == 4 )
                        return plateau[i][j] == 'O'? FinDePartie.ORDI_GAGNE : FinDePartie.HUMAIN_GAGNE;

                }
            }
        }
        if(n == COLONNE*LIGNE) return FinDePartie.MATCHNUL;
        return FinDePartie.NON;
    }

    public void ordijoue_mcts(int tempsmax){
        long tic, toc;
        tic = System.currentTimeMillis();
        int temps;

        List<Coup> coups;
        Coup meilleur_coup;

        //Créer l'arbre de recherche
        Noeud racine = new Noeud();
        racine.nouveauNoeud(null);
        racine.setEtat(copieEtat());

        //Créer les premiers noeuds:
        coups = coupsPossibles();
        Noeud enfant = new Noeud();
        for (Coup coup: coups) {
            enfant = enfant.ajouterEnfant(coup);
        }

        int r = new Random().nextInt(7);
        meilleur_coup = coups.get(r);

        jouerCoup(meilleur_coup);
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
