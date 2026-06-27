#!/usr/bin/env bash
set -uo pipefail
cd "$(dirname "$0")"

SERVICES=(redis config auth-public catalog gateway order mercado-pago ngrok)
MAX_WAIT=90
INTERVAL=3

GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${YELLOW}==> Levantando stack DTT...${NC}"
docker compose up -d

FAILED=()

for service in "${SERVICES[@]}"; do
  echo -ne "${YELLOW}Verificando ${service}...${NC}"
  elapsed=0
  ok=false
  status=""
  health=""

  while [ $elapsed -lt $MAX_WAIT ]; do
    container=$(docker compose ps -q "$service" 2>/dev/null)
    if [ -z "$container" ]; then
      sleep $INTERVAL; elapsed=$((elapsed + INTERVAL)); continue
    fi

    status=$(docker inspect --format='{{.State.Status}}' "$container" 2>/dev/null)
    health=$(docker inspect --format='{{if .State.Health}}{{.State.Health.Status}}{{else}}none{{end}}' "$container" 2>/dev/null)

    if [ "$status" = "running" ] && { [ "$health" = "healthy" ] || [ "$health" = "none" ]; }; then
      ok=true; break
    fi

    sleep $INTERVAL
    elapsed=$((elapsed + INTERVAL))
  done

  if [ "$ok" = true ]; then
    echo -e "\r${GREEN}✓ ${service}: OK (${health})${NC}                         "
  else
    echo -e "\r${RED}✗ ${service}: FALLÓ (status=${status}, health=${health})${NC}"
    FAILED+=("$service")
  fi
done

echo ""
if [ ${#FAILED[@]} -gt 0 ]; then
  echo -e "${RED}==> Servicios con problemas: ${FAILED[*]}${NC}"
  for service in "${FAILED[@]}"; do
    echo -e "${YELLOW}--- Logs de ${service} ---${NC}"
    docker compose logs --tail=30 "$service"
    echo ""
  done
  exit 1
else
  echo -e "${GREEN}==> Stack completo arriba y saludable ✅${NC}"
  docker compose ps
fi
