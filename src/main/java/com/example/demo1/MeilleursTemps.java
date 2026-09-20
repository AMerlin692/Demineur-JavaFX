package com.example.demo1;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class MeilleursTemps {
    private static final Path FICHIER = Path.of(System.getProperty("user.home"), ".demineur", "meilleurs_temps.properties");

    private final Properties donnees = new Properties();

    public MeilleursTemps() {
        charger();
    }

    private void charger() {
        if (!Files.exists(FICHIER)) return;
        try (InputStream in = Files.newInputStream(FICHIER)) {
            donnees.load(in);
        } catch (IOException e) {
            System.err.println("Lecture des records impossible : " + e.getMessage());
        }
    }

    private void sauvegarder() {
        try {
            Files.createDirectories(FICHIER.getParent());
            try (OutputStream out = Files.newOutputStream(FICHIER)) {
                donnees.store(out, "Meilleurs temps du demineur (en secondes)");
            }
        } catch (IOException e) {
            System.err.println("Sauvegarde des records impossible : " + e.getMessage());
        }
    }

    /** Meilleur temps en secondes pour ce niveau, ou -1 s'il n'y en a pas encore. */
    public int get(Niveau niveau) {
        String valeur = donnees.getProperty(niveau.name());
        if (valeur == null) return -1;
        try {
            return Integer.parseInt(valeur.trim());
        } catch (NumberFormatException e) {
            return -1; // fichier modifié à la main ou corrompu : on l'ignore
        }
    }

    /** Enregistre le temps s'il bat le record. Renvoie true si c'est un nouveau record. */
    public boolean enregistrer(Niveau niveau, int secondes) {
        int record = get(niveau);
        if (record == -1 || secondes < record) {
            donnees.setProperty(niveau.name(), String.valueOf(secondes));
            sauvegarder();
            return true;
        }
        return false;
    }
}