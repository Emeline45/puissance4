package fr.ul.puissance4;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Coup coup;
        FinDePartie fin;

        //Initialisation
        Etat etat = new Etat();

        //Choisir qui commence :
        System.out.printf("Qui commence (%d : humain, %d : ordinateur) ? ", Etat.HUMAN_PLAYER, Etat.COMPUTER_PLAYER);
        Scanner scanner = new Scanner(System.in);
        etat.setJoueur(scanner.nextInt());

        //boucle de jeu
        do {
          System.out.println(etat);

           if(etat.getJoueur() == Etat.HUMAN_PLAYER) {
               //tour de l'humain
               do {
                   coup = Coup.demanderCoup();
               } while (!etat.jouerCoup(coup));
           } else {
               //tour de l'ordinateur
               etat.ordijoue_mcts(5000 /* 5s */);
           }
        } while ((fin = etat.testFin()) == FinDePartie.NON);

        System.out.println(etat);

        if (fin == FinDePartie.ORDI_GAGNE) System.out.println("**L'ordinateur a gagné **");
        else if (fin == FinDePartie.MATCH_NUL) System.out.println("Match nul !");
        else System.out.println("**Bravo, l'ordinateur a perdu **");

    }


}
