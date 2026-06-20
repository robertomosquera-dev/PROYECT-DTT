#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"

echo "==> Apagando stack DTT..."
docker compose down

echo "==> Listo. Contenedores detenidos y removidos (volúmenes intactos)."
