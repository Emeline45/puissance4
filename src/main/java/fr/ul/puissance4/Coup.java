package fr.ul.puissance4;

import java.util.Objects;
import java.util.Scanner;

public class Coup {
    /* Le joueur ne peut choisir que la colonne dans le puissance 4.*/
    private final int colonne;

    public Coup(int j){
        colonne = j;
    }

    /**
     * Récupère la colonne jouée dans le coup
     * @return le numéro de la colonne
     */
    public int getColonne() {
        return colonne;
    }

    /**
     * Demande à l'humain quel coup jouer
     * @return le Coup demandé
     */
    public static Coup demanderCoup(){
        System.out.println("\n\tQuelle colonne ? ");
        Scanner scanner = new Scanner(System.in);
        return new Coup(scanner.nextInt()-1);
    }

    @Override
    public String toString() {
        return "Coup{" +
                "colonne=" + colonne +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coup coup = (Coup) o;
        return colonne == coup.colonne;
    }

    @Override
    public int hashCode() {
        return Objects.hash(colonne);
    }
}
