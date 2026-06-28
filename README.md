# PROYECT-DTT
# Configuración de n8n

## Requisitos previos
- Stack DTT levantado con `./run.sh`
- n8n accesible en `http://localhost:5678`

## Paso 1 — Instalar el nodo de Resend

1. Ir a **Settings → Community nodes**
2. Click en **Install**
3. Escribir `n8n-nodes-resend`
4. Click en **Install**
5. Esperar que se instale y reiniciar n8n si lo pide

## Paso 2 — Crear la credencial de Resend

1. Ir a **Settings → Credentials**
2. Click en **Add credential**
3. Buscar **Resend**
4. En el campo **API Key** pegar el valor de `RESEND_API_KEY` del `.env`
5. Click en **Save**

## Paso 3 — Importar los workflows

Los workflows están en `docker/services/n8n/workflows/`.

1. Ir a **Workflows** en el sidebar
2. Click en el botón **+** → **Import from file**
3. Importar `send-code-verification.json`
4. Repetir para `order-creation-notification.json`
5. Repetir para `order-payment-confirmation.json`

## Paso 4 — Configurar credencial en cada workflow

Por cada workflow importado:

1. Abrir el workflow
2. Click en el nodo **Send a new email**
3. En el campo **Credential** seleccionar la credencial de Resend creada en el Paso 2
4. Click en **Save**

## Paso 5 — Activar los workflows

Por cada workflow:

1. Click en **Publish** arriba a la derecha
2. Confirmar que el toggle queda en activo

## Verificación

Los 3 workflows deben aparecer como activos en la lista de workflows.
