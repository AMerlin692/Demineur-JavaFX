package com.example.demo1;

import java.util.Random;

public class Plateau {

    private final int lignes;
    private final int colonnes;
    private final int nbMines;
    private final Case[][] grille;
    private int casesRevelees = 0;
    private boolean minesPlacees = false;

    public Plateau(int lignes, int colonnes, int nbMines) {
        if (nbMines > lignes * colonnes - 9) {
            throw new IllegalArgumentException("Trop de mines pour cette grille");
        }
        this.lignes = lignes;
        this.colonnes = colonnes;
        this.nbMines = nbMines;
        this.grille = new Case[lignes][colonnes];

        for (int l = 0; l < lignes; l++) {
            for (int c = 0; c < colonnes; c++) {
                grille[l][c] = new Case();
            }
        }
    }

    public int getNbMines() { return nbMines; }

    private void placerMines(int ligneSure, int colonneSure) {
        Random random = new Random();
        int placees = 0;
        while (placees < nbMines) {
            int l = random.nextInt(lignes);
            int c = random.nextInt(colonnes);
            boolean zoneSure = Math.abs(l - ligneSure) <= 1
                    && Math.abs(c - colonneSure) <= 1;
            if (!zoneSure && !grille[l][c].estMine()) {
                grille[l][c].setMine(true);
                placees++;
            }
        }
    }

    public boolean estDansLaGrille(int l, int c) {
        return l >= 0 && l < lignes && c >= 0 && c < colonnes;
    }

    private void calculerVoisines() {
        for (int l = 0; l < lignes; l++) {
            for (int c = 0; c < colonnes; c++) {
                if (grille[l][c].estMine()) continue;
                int compteur = 0;
                for (int dl = -1; dl <= 1; dl++) {
                    for (int dc = -1; dc <= 1; dc++) {
                        if (dl == 0 && dc == 0) continue;
                        int nl = l + dl, nc = c + dc;
                        if (estDansLaGrille(nl, nc) && grille[nl][nc].estMine()) {
                            compteur++;
                        }
                    }
                }
                grille[l][c].setMinesVoisines(compteur);
            }
        }
    }

    public void afficherTout() {
        for (int l = 0; l < lignes; l++) {
            for (int c = 0; c < colonnes; c++) {
                Case cell = grille[l][c];
                System.out.print(cell.estMine() ? "* " : cell.getMinesVoisines() + " ");
            }
            System.out.println();
        }
    }

    public int getLignes() {
        return lignes;
    }

    public int getColonnes() {
        return colonnes;
    }

    public Case getCase(int l, int c) {
        return grille[l][c];
    }

    public void afficher() {
        System.out.print("   ");
        for (int c = 0; c < colonnes; c++) {
            System.out.printf("%2d ", c);
        }
        System.out.println();

        for (int l = 0; l < lignes; l++) {
            System.out.printf("%2d ", l);
            for (int c = 0; c < colonnes; c++) {
                System.out.print(" " + symbole(grille[l][c]) + " ");
            }
            System.out.println();
        }
    }

    private char symbole(Case cell) {
        if (!cell.estRevelee()) {
            return cell.aDrapeau() ? 'F' : '#';
        }
        if (cell.estMine()) return '*';
        if (cell.getMinesVoisines() == 0) return '.';
        return (char) ('0' + cell.getMinesVoisines());
    }

    public boolean reveler(int l, int c) {
        if (!estDansLaGrille(l, c)) return false;

        if (!minesPlacees) {
            placerMines(l, c);
            calculerVoisines();
            minesPlacees = true;
        }
        return revelerCase(l, c);
    }

    private boolean revelerCase(int l, int c) {
        if (!estDansLaGrille(l, c)) return false;

        Case cell = grille[l][c];
        if (cell.estRevelee() || cell.aDrapeau()) return false;

        cell.setRevelee(true);
        casesRevelees++;

        if (cell.estMine()) return true;

        if (cell.getMinesVoisines() == 0) {
            for (int dl = -1; dl <= 1; dl++) {
                for (int dc = -1; dc <= 1; dc++) {
                    if (dl == 0 && dc == 0) continue;
                    revelerCase(l + dl, c + dc);
                }
            }
        }
        return false;
    }

    public void basculerDrapeau(int l, int c) {
        if (!estDansLaGrille(l, c)) return;
        Case cell = grille[l][c];
        if (!cell.estRevelee()) {
            cell.basculerDrapeau();
        }
    }

    public boolean estGagne() {
        return casesRevelees == lignes * colonnes - nbMines;
    }

    public void revelerToutesLesMines() {
        for (int l = 0; l < lignes; l++) {
            for (int c = 0; c < colonnes; c++) {
                if (grille[l][c].estMine()) {
                    grille[l][c].setRevelee(true);
                }
            }
        }
    }
}
