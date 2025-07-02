#!/bin/bash

echo "Wybierz do którego remote chcesz wypchnąć kod:"
echo "1) gitea"
echo "2) github"
echo "3) oba"

read -rp "Wpisz numer opcji (1-3): " opcja

case $opcja in
  1)
    echo "Pushing to gitea..."
    git push gitea main
    ;;
  2)
    echo "Pushing to github..."
    git push github main
    ;;
  3)
    echo "Pushing to both remotes..."
    git push gitea main && git push github main
    ;;
  *)
    echo "Niepoprawna opcja."
    exit 1
    ;;
esac

