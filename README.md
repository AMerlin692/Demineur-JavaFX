# 💣 Démineur JavaFX

Un jeu de démineur complet en Java, avec interface graphique JavaFX, écran d'accueil, écran de fin et sauvegarde des meilleurs temps.

<!-- Ajoute ici une capture d'écran : ![Aperçu du jeu](docs/apercu.png) -->

## Fonctionnalités

- Trois niveaux de difficulté (voir le tableau ci-dessous)
- Premier clic toujours sûr : les mines sont placées après le premier clic, en épargnant la case choisie et ses voisines
- Propagation automatique des zones vides
- Drapeaux, compteur de mines restantes et chronomètre
- Écran d'accueil pour choisir le niveau, écran de fin avec le résultat, le temps et le record
- Meilleur temps par niveau, conservé d'une session à l'autre
- Version console jouable au clavier

## Niveaux

| Niveau        | Grille   | Mines |
|---------------|----------|-------|
| Débutant      | 9 × 9    | 10    |
| Intermédiaire | 16 × 16  | 40    |
| Expert        | 16 × 30  | 99    |

## Commandes

| Action           | Interface graphique | Version console        |
|------------------|---------------------|------------------------|
| Révéler une case | Clic gauche         | `r ligne colonne`      |
| Poser un drapeau | Clic droit          | `f ligne colonne`      |
| Quitter          | Bouton « Menu »     | `q`                    |

## Prérequis

- JDK 17 ou plus récent
- Maven (intégré à IntelliJ IDEA)
- Aucune installation manuelle de JavaFX : Maven le télécharge automatiquement

## Installation et lancement

### Avec IntelliJ IDEA

1. Récupère le projet : **File → New → Project from Version Control**, puis colle l'URL du dépôt GitHub.
2. Laisse IntelliJ importer le projet Maven et attends la fin du chargement (si besoin, clique sur **Load Maven Changes**).
3. Vérifie que le SDK du projet est un JDK 17 ou plus : **File → Project Structure → Project**.
4. Ouvre `DemineurApp` et clique sur le triangle vert à côté de la méthode `main`.

### En ligne de commande

```bash
git clone https://github.com/Manof692/Demineur-JavaFX
cd Demineur-JavaFX
chmod +x start.sh
./start.sh
```

## Structure du projet

```
src/main/java/<package>/
├── Case.java          # Une case : mine, révélée, drapeau, mines voisines
├── Plateau.java       # La grille et les règles du jeu (aucune dépendance à l'affichage)
├── Niveau.java        # Enum des niveaux de difficulté
├── MeilleursTemps.java# Lecture et écriture des records dans un fichier
├── DemineurApp.java   # Interface JavaFX : accueil, jeu, fin de partie
├── Partie.java        # Boucle de jeu de la version console
└── Main.java          # Point d'entrée de la version console
```

## Choix techniques

- **Modèle séparé de l'affichage** : `Plateau` et `Case` ne connaissent ni la console ni JavaFX. Les deux interfaces réutilisent exactement la même logique.
- **Propagation récursive** : révéler une case sans mine voisine révèle automatiquement ses huit voisines, de proche en proche.
- **Placement différé des mines** : elles sont tirées au hasard au premier clic, ce qui garantit qu'on ne perd jamais dès le premier coup.
- **`enum Niveau`** : chaque niveau porte ses dimensions et son nombre de mines, et le menu se construit automatiquement à partir de `Niveau.values()`.
- **Sauvegarde des records** : `java.util.Properties`, dans le fichier `meilleurs_temps.properties`, situé dans le dossier `.demineur` du répertoire personnel de l'utilisateur. Supprimer ce fichier remet tous les records à zéro.

## Pistes d'amélioration

- [ ] Sortir les styles dans un fichier CSS
- [ ] Niveau personnalisé (lignes, colonnes et mines au choix)
- [ ] Révélation des voisines en cliquant sur un chiffre (*chording*)
- [ ] Propagation itérative avec une `Deque` pour éviter tout risque de `StackOverflowError`

## Auteur

Projet personnel réalisé pour m'entraîner à la programmation orientée objet en Java.

**Alan MERLIN**
