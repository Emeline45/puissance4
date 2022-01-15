package fr.ul.puissance4;

import java.util.ArrayList;
import java.util.List;

public class Etat {
    public static final int LIGNE = 6;
    public static final int COLONNE = 7;

    public static final int HUMAN_PLAYER = 0;
    public static final int COMPUTER_PLAYER = 1;

    private int joueur; //à qui de jouer
    private char[][] plateau; //ligne/colonne

    /**
     * Constructeur de l'fr.ul.puissance4.Etat initial
     */
    public Etat() {
        joueur = COMPUTER_PLAYER; //L'ordinateur qui commence :)
        plateau = new char[LIGNE][COLONNE]; //plateau du Puissance 4

        //Initialisation du plateau vide
        int i,j;
        for(i=0; i < LIGNE; i++){
            for (j = 0; j <COLONNE; j++){
                this.plateau[i][j] = ' ';
            }
        }
    }

    public Etat(int j, char[][] tab) {
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
     * Copie de l'fr.ul.puissance4.Etat en cours
     *
     * @return un fr.ul.puissance4.Etat
     */
    public Etat copieEtat() {
        char[][] tab = new char[LIGNE][COLONNE];
        for(int i = 0; i< LIGNE; i++){
            System.arraycopy(plateau[i], 0, tab[i], 0, COLONNE);
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
        if (coup == null)
            return false;

        //verifie que la ligne n'est pas pleine
        if(this.getPlateau()[0][coup.getColonne()] != ' ')
            return false;

        this.getPlateau()[0][coup.getColonne()] = joueur == HUMAN_PLAYER ? 'O' : 'X' ;
        for (int i = 1; i < LIGNE; i++){
            if(this.getPlateau()[i][coup.getColonne()] == ' ') {
                this.getPlateau()[i][coup.getColonne()] = joueur == HUMAN_PLAYER ? 'O' : 'X';
                this.getPlateau()[i-1][coup.getColonne()] = ' ' ;
            }
        }
        //while (i< fr.ul.puissance4.Etat.LIGNE && this.getPlateau()[i++][coup.getColonne()] == ' ');
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
                        return plateau[i][j] == 'X'? FinDePartie.ORDI_GAGNE : FinDePartie.HUMAIN_GAGNE;

                }
            }
        }
        if(n == COLONNE*LIGNE) return FinDePartie.MATCH_NUL;
        return FinDePartie.NON;
    }

    public void ordijoue_mcts(long tempsmax){
        long tic, toc;
        tic = System.currentTimeMillis();
        long temps;

        List<Coup> coups = coupsPossibles();

        //Créer l'arbre de recherche
        Noeud racine = Noeud.nouveauNoeud(null,null);
        racine.setEtat(copieEtat());

        //Créer les premiers noeuds:
//        for (fr.ul.puissance4.Coup coup: coups) {
//            enfant = racine.ajouterEnfant(coup);
//
//            //On vérifie si au tour suivant, l'ordi peut gagner
//            //Ça en deviendra le meilleur coup
//            fr.ul.puissance4.FinDePartie fin = enfant.getEtat().testFin();
//            if(fin == fr.ul.puissance4.FinDePartie.ORDI_GAGNE){
//                meilleur_coup = coup;
//            }else{
//                //Faire d'autre vérification
//            }
//        }
        do {
            Noeud toExpand = selection(racine);
            if (toExpand == null)
                break; // l'arbre a été complètement exploré, plus aucun noeud n'est sélectionnable

            Noeud expanded = expansion(toExpand);
            FinDePartie status = simulation(expanded);
            expanded.propagationScore();

            toc = System.currentTimeMillis();
            temps = (toc - tic);
        } while (temps < tempsmax);

        Coup best = null;
        double val = Double.NEGATIVE_INFINITY;
        for (Noeud enf : racine.getEnfant()) {
            double val2 = enf.ratio();
            if (val2 > val) {
                val = val2;
                best = enf.getCoup();
            }
        }

        jouerCoup(best);
    }

    private Noeud selection(Noeud racine) {
        return racine; // TODO
    }

    private Noeud expansion(Noeud node) {
        return node; // TODO
    }

    private FinDePartie simulation(Noeud racineLocale) {
        return FinDePartie.NON; // TODO
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
            for ( j = 0; j < COLONNE; j++) {
                string.append(" ");
                char x = plateau[i-1][j];
                string.append(x == 'X' ? "\033[41m\033[31mX" : x == 'O' ? "\033[43m\033[33mO" : " ").append("\033[0m");
                string.append(j == COLONNE - 1 ? " ┃" : " │");
            }
            string.append("\n").append(i==LIGNE ? "━━━┻━━━┷━━━┷━━━┷━━━┷━━━┷━━━┷━━━┛" : "━━━╉───┼───┼───┼───┼───┼───┼───┨").append("\n");
        }
        return string.toString();
    }
}
