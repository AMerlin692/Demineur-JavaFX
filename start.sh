#!/usr/bin/env bash
# Lance le démineur JavaFX via Maven.
# À placer à la racine du projet, à côté du fichier pom.xml.

set -euo pipefail

# Se placer dans le dossier du script, quel que soit l'endroit d'où il est appelé
cd "$(dirname "${BASH_SOURCE[0]}")"

if [ ! -f pom.xml ]; then
    echo "Erreur : pom.xml introuvable dans $(pwd)." >&2
    echo "Place start.sh à la racine du projet." >&2
    exit 1
fi

# Vérifier que Java est installé
if ! command -v java >/dev/null 2>&1; then
    echo "Erreur : Java est introuvable. Installe un JDK 17 ou plus récent." >&2
    exit 1
fi

# Vérifier que la version de Java est suffisante (17 minimum)
VERSION_JAVA=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d. -f1)
if [ "$VERSION_JAVA" -lt 17 ] 2>/dev/null; then
    echo "Erreur : Java $VERSION_JAVA détecté, il faut Java 17 ou plus récent." >&2
    exit 1
fi

# Utiliser le wrapper Maven du projet s'il existe, sinon le Maven du système
if [ -x ./mvnw ]; then
    MVN=./mvnw
elif command -v mvn >/dev/null 2>&1; then
    MVN=mvn
else
    echo "Erreur : Maven est introuvable (ni ./mvnw, ni mvn dans le PATH)." >&2
    exit 1
fi

echo "Lancement du démineur..."
exec "$MVN" -q javafx:run
