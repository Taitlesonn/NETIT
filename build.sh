#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

# -----------------------------------------------------------------------------
# Konfiguracja — możesz nadpisać przez zmienne środowiskowe:
#   JAVA_FX_SDK  – ścieżka do katalogu lib JavaFX
#   MAVEN_ARGS   – dodatkowe argumenty do mvn
#   JAVA_ARGS    – dodatkowe argumenty do java
# -----------------------------------------------------------------------------

# Domyślna ścieżka do JavaFX (możesz nadpisać przez export JAVA_FX_SDK=…)
JAVA_FX_SDK="${JAVA_FX_SDK:-/opt/javafx-sdk-21/javafx-sdk-21.0.7/lib}"
# Domyślne, puste wartości dla opcjonalnych zmiennych
MAVEN_ARGS="${MAVEN_ARGS:-}"
JAVA_ARGS="${JAVA_ARGS:-}"

# Nazwy plików i katalogów
PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
OUT_DIR="$PROJECT_DIR/out"
TARGET_JAR="$PROJECT_DIR/target/original-netit-1.0.jar"
OUTPUT_JAR="$OUT_DIR/netit.jar"

# Proste funkcje pomocnicze:
log()    { echo -e "\e[1;34m[INFO]\e[0m $*"; }
warn()   { echo -e "\e[1;33m[WARN]\e[0m $*" >&2; }
error()  { echo -e "\e[1;31m[ERROR]\e[0m $*" >&2; exit 1; }
usage()  {
  cat <<EOF
Użycie: $(basename "$0") [--clean] [--help]

  --clean     → usuń katalog out i target przed budową
  --help      → wyświetla tę pomoc

Zmiennymi środowiskowymi można kontrolować:
  JAVA_FX_SDK   ścieżka do JavaFX/lib (domyślnie $JAVA_FX_SDK)
  MAVEN_ARGS    dodatkowe flagi do mvn
  JAVA_ARGS     dodatkowe flagi do java
EOF
  exit 0
}

# Parsowanie opcji
CLEAN=false
while [[ $# -gt 0 ]]; do
  case $1 in
    --clean) CLEAN=true; shift;;
    --help)  usage;;
    *)       error "Nieznana opcja: $1";;
  esac
done

# Opcjonalne czyszczenie
if $CLEAN; then
  log "Czyszczenie katalogów target i out..."
  rm -rf "$PROJECT_DIR/target" "$OUT_DIR"
fi

# Faza 1: budowa Maven
log "Buduję projekt z Mavenem…"
mvn $MAVEN_ARGS clean package

# Faza 2: przygotowanie katalogu out
log "Przygotowuję katalog wyjściowy: $OUT_DIR"
mkdir -p "$OUT_DIR"
[[ -f "$TARGET_JAR" ]] || error "Nie odnaleziono JAR-a: $TARGET_JAR"

log "Kopiuję folder 'files' do katalogu $OUT_DIR"
cp -av "$PROJECT_DIR/files" "$OUT_DIR/"

log "Kopiuję JAR do $OUT_DIR jako $OUTPUT_JAR"
cp "$TARGET_JAR" "$OUTPUT_JAR"


# Faza 3: uruchomienie aplikacji
log "Uruchamiam aplikację JavaFX…"
java \
  --module-path "$JAVA_FX_SDK" \
  --add-modules javafx.controls,javafx.fxml,javafx.web \
  $JAVA_ARGS \
  -jar "$OUTPUT_JAR"
