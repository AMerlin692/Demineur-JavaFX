package com.example.demo1;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class DemineurApp extends Application {
    private static final int TAILLE_CASE = 32;
    private static final String[] COULEURS = {
            "black", "#0000ff", "#008000", "#ff0000", "#000080",
            "#800000", "#008080", "#000000", "#808080"
    };

    private Stage stage;
    private Scene scene;
    private BorderPane ecranJeu;
    private final Label labelMines = new Label();
    private final Label labelTemps = new Label();
    private Timeline chrono;
    private PauseTransition pauseFin;
    private int secondes;

    private Niveau niveau;
    private Plateau plateau;
    private Button[][] boutons;
    private boolean partieTerminee;

    private final MeilleursTemps meilleursTemps = new MeilleursTemps();
    private boolean nouveauRecord;

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        chrono = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            secondes++;
            labelTemps.setText("Temps : " + secondes + " s");
        }));
        chrono.setCycleCount(Timeline.INDEFINITE);

        scene = new Scene(creerAccueil());
        stage.setTitle("Démineur");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    // ==================== Écrans ====================

    private Parent creerAccueil() {
        Label titre = new Label("💣 DÉMINEUR");
        titre.setStyle("-fx-font-size: 36px; -fx-font-weight: bold;");

        Label sousTitre = new Label("Choisis un niveau pour commencer");
        sousTitre.setStyle("-fx-font-size: 14px; -fx-text-fill: #666666;");

        VBox niveaux = new VBox(12);
        niveaux.setAlignment(Pos.CENTER);
        for (Niveau n : Niveau.values()) {
            Button bouton = new Button(n.toString());
            bouton.setPrefWidth(280);
            bouton.setOnAction(e -> lancerPartie(n));

            int record = meilleursTemps.get(n);
            Label labelRecord = new Label(record == -1 ? "Record : —" : "Record : " + record + " s");
            labelRecord.setStyle("-fx-font-size: 11px; -fx-text-fill: #888888;");

            VBox bloc = new VBox(3, bouton, labelRecord);
            bloc.setAlignment(Pos.CENTER);
            niveaux.getChildren().add(bloc);
        }

        Label aide = new Label("Clic gauche : révéler   •   Clic droit : drapeau");
        aide.setStyle("-fx-font-size: 12px; -fx-text-fill: #888888;");

        Button quitter = new Button("Quitter");
        quitter.setOnAction(e -> Platform.exit());

        VBox accueil = new VBox(20, titre, sousTitre, niveaux, aide, quitter);
        accueil.setAlignment(Pos.CENTER);
        accueil.setPadding(new Insets(40));
        accueil.setPrefWidth(420);
        return accueil;
    }

    private Parent creerEcranFin(boolean gagne) {
        Label titre = new Label(gagne ? "🎉 VICTOIRE !" : "💥 PERDU !");
        titre.setStyle("-fx-font-size: 34px; -fx-font-weight: bold;"
                + " -fx-text-fill: " + (gagne ? "#2e7d32" : "#c62828") + ";");

        Label message = new Label(gagne
                ? "Toutes les mines ont été évitées, bien joué !"
                : "Tu as touché une mine.");
        message.setStyle("-fx-font-size: 14px;");

        Label labelNiveau = new Label("Niveau : " + niveau);
        Label tempsFinal = new Label("Temps : " + secondes + " s");
        labelNiveau.setStyle("-fx-text-fill: #555555;");
        tempsFinal.setStyle("-fx-text-fill: #555555;");

        int record = meilleursTemps.get(niveau);
        Label labelRecord = new Label();
        if (nouveauRecord) {
            labelRecord.setText("🏆 Nouveau record !");
            labelRecord.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #f9a825;");
        } else {
            labelRecord.setText(record == -1
                    ? "Pas encore de record à ce niveau"
                    : "Meilleur temps : " + record + " s");
            labelRecord.setStyle("-fx-text-fill: #555555;");
        }

        Button rejouer = new Button("Rejouer");
        rejouer.setDefaultButton(true);
        rejouer.setOnAction(e -> {
            nouvellePartie();
            afficher(ecranJeu);
        });

        Button voirGrille = new Button("Voir la grille");
        voirGrille.setOnAction(e -> afficher(ecranJeu));

        Button changerNiveau = new Button("Changer de niveau");
        changerNiveau.setOnAction(e -> retourAccueil());

        Button quitter = new Button("Quitter");
        quitter.setOnAction(e -> Platform.exit());

        for (Button b : new Button[] { rejouer, voirGrille, changerNiveau, quitter }) {
            b.setPrefWidth(220);
        }

        VBox fin = new VBox(15, titre, message, labelNiveau, tempsFinal, labelRecord, rejouer, voirGrille, changerNiveau, quitter);
        fin.setAlignment(Pos.CENTER);
        fin.setPadding(new Insets(40));
        fin.setPrefWidth(420);
        return fin;
    }

    private void afficher(Parent racine) {
        scene.setRoot(racine);
        stage.sizeToScene();
        stage.centerOnScreen();
    }

    private void retourAccueil() {
        chrono.stop();
        if (pauseFin != null) pauseFin.stop();
        afficher(creerAccueil());
    }

    // ==================== Jeu ====================

    private void lancerPartie(Niveau niveauChoisi) {
        this.niveau = niveauChoisi;

        Button boutonMenu = new Button("Menu");
        boutonMenu.setOnAction(e -> retourAccueil());

        Button boutonNouvelle = new Button("Nouvelle partie");
        boutonNouvelle.setOnAction(e -> nouvellePartie());

        Region espace = new Region();
        HBox.setHgrow(espace, Priority.ALWAYS);

        HBox barre = new HBox(10, boutonMenu, boutonNouvelle, espace, labelMines, labelTemps);
        barre.setPadding(new Insets(10));
        barre.setAlignment(Pos.CENTER_LEFT);

        ecranJeu = new BorderPane();
        ecranJeu.setTop(barre);

        nouvellePartie();
        afficher(ecranJeu);
    }

    private void nouvellePartie() {
        if (pauseFin != null) pauseFin.stop();
        chrono.stop();
        secondes = 0;
        labelTemps.setText("Temps : 0 s");

        plateau = new Plateau(niveau.getLignes(), niveau.getColonnes(), niveau.getNbMines());
        boutons = new Button[plateau.getLignes()][plateau.getColonnes()];
        partieTerminee = false;

        GridPane grille = new GridPane();
        grille.setPadding(new Insets(0, 10, 10, 10));
        grille.setHgap(2);
        grille.setVgap(2);
        grille.setAlignment(Pos.CENTER);

        for (int l = 0; l < plateau.getLignes(); l++) {
            for (int c = 0; c < plateau.getColonnes(); c++) {
                Button bouton = new Button();
                bouton.setMinSize(TAILLE_CASE, TAILLE_CASE);
                bouton.setMaxSize(TAILLE_CASE, TAILLE_CASE);

                final int ligne = l;
                final int colonne = c;
                bouton.setOnMouseClicked(event -> {
                    if (partieTerminee) return;

                    if (event.getButton() == MouseButton.PRIMARY) {
                        if (chrono.getStatus() != Animation.Status.RUNNING) {
                            chrono.play();
                        }
                        boolean mine = plateau.reveler(ligne, colonne);
                        if (mine) {
                            terminer(false);
                            return;
                        }
                        if (plateau.estGagne()) {
                            terminer(true);
                            return;
                        }
                    } else if (event.getButton() == MouseButton.SECONDARY) {
                        plateau.basculerDrapeau(ligne, colonne);
                    }
                    rafraichir();
                });

                boutons[l][c] = bouton;
                grille.add(bouton, c, l);
            }
        }

        ecranJeu.setCenter(grille);
        rafraichir();
        stage.sizeToScene();
    }

    private void terminer(boolean gagne) {
        partieTerminee = true;
        chrono.stop();
        nouveauRecord = gagne && meilleursTemps.enregistrer(niveau, secondes);
        if (!gagne) {
            plateau.revelerToutesLesMines();
        }
        rafraichir();

        // Petit délai pour que le joueur voie la grille avant l'écran de fin
        pauseFin = new PauseTransition(Duration.seconds(gagne ? 0.6 : 1.2));
        pauseFin.setOnFinished(e -> afficher(creerEcranFin(gagne)));
        pauseFin.play();
    }

    private void rafraichir() {
        int drapeaux = 0;

        for (int l = 0; l < plateau.getLignes(); l++) {
            for (int c = 0; c < plateau.getColonnes(); c++) {
                Case cell = plateau.getCase(l, c);
                Button bouton = boutons[l][c];

                if (!cell.estRevelee()) {
                    if (cell.aDrapeau()) drapeaux++;
                    bouton.setText(cell.aDrapeau() ? "🚩" : "");
                    bouton.setStyle("");
                } else if (cell.estMine()) {
                    bouton.setText("💣");
                    bouton.setStyle("-fx-background-color: #ff8080;");
                } else {
                    int n = cell.getMinesVoisines();
                    bouton.setText(n == 0 ? "" : String.valueOf(n));
                    bouton.setStyle("-fx-background-color: #d8d8d8;"
                            + " -fx-font-weight: bold;"
                            + " -fx-text-fill: " + COULEURS[n] + ";");
                }
            }
        }
        labelMines.setText("Mines : " + (plateau.getNbMines() - drapeaux));
    }

    public static void main(String[] args) {
        launch(args);
    }
}