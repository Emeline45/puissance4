package fr.ul.puissance4;

import java.util.Scanner;

public class Coup {
    /* Le joueur ne peut choisir que la colonne dans le puissance 4.*/
    private int colonne;

    public Coup(){colonne = -1;}

    public Coup(int j){
        colonne = j;
    }

    public int getColonne() {
        return colonne;
    }

    public void setColonne(int colonne) {
        this.colonne = colonne;
    }

    /**
     * Demande à l'humain quel coup jouer
     * @return le Coup demandé
     */
    public static Coup demanderCoup(){
        System.out.println("\n \t Quelle colonne ? ");
        Scanner scanner = new Scanner(System.in);
        return new Coup(scanner.nextInt()-1);
    }

}
