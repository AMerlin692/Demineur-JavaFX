package com.example.demo1;

public enum Niveau {
    DEBUTANT("Débutant", 9, 9, 10),
    INTERMEDIAIRE("Intermédiaire", 16, 16, 40),
    EXPERT("Expert", 16, 30, 99);

    private final String libelle;
    private final int lignes;
    private final int colonnes;
    private final int nbMines;

    Niveau(String libelle, int lignes, int colonnes, int nbMines) {
        this.libelle = libelle;
        this.lignes = lignes;
        this.colonnes = colonnes;
        this.nbMines = nbMines;
    }

    public int getLignes() { return lignes; }
    public int getColonnes() { return colonnes; }
    public int getNbMines() { return nbMines; }

    @Override
    public String toString() {
        return libelle + " (" + lignes + "x" + colonnes + ", " + nbMines + " mines)";
    }
}
