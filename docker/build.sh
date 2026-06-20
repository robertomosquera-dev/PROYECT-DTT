#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"

NOCACHE=""
SERVICE=""

for arg in "$@"; do
  if [ "$arg" = "--no-cache" ]; then
    NOCACHE="--no-cache"
  else
    SERVICE="$arg"
  fi
done

if [ -z "$SERVICE" ]; then
  echo "==> Reconstruyendo TODAS las imágenes..."
  docker compose build $NOCACHE
  docker compose up -d --force-recreate
else
  echo "==> Reconstruyendo imagen de '${SERVICE}'..."
  docker compose build $NOCACHE "$SERVICE"
  docker compose up -d --force-recreate "$SERVICE"
fi

echo "==> Listo."
docker compose ps
