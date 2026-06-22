#!/bin/bash
# ─────────────────────────────────────────────
#  prefixsapp_log.sh — Habilita y muestra logs ADB
#  Dispositivo: Honor CRT-NX3 (Android 15)
#  Proyecto  : PrefixApp / NoMeLlames
# ─────────────────────────────────────────────
#
#  ⚠ IMPORTANTE:
#  Timber solo se planta en builds DEBUG (BuildConfig.DEBUG = true).
#  Las variantes "release" y "apk" no emiten logs de Timber
#  a menos que se modifique PrefixedApp para plantar un Tree
#  en todas las variantes.
#
#  El servicio de screening (SpamCallPrefixService) corre en
#  el proceso de la app pero se bindea por el sistema en cada
#  llamada. Se filtra por PID para no perder los logs de
#  onScreenCall.
# ─────────────────────────────────────────────

set -e

PACKAGES=(
  "cl.blipblipcode.prefixsapp.debug"
  "cl.blipblipcode.prefixsapp.apk"
  "cl.blipblipcode.prefixsapp"
)

# Tags reales usados por Timber en el proyecto (nombre de clase)
TIMBER_TAGS="SpamCallPrefixService|SpamCallPrefixService\\\$onScreenCall|ContactsRepositoryImpl|VersionRepositoryImpl|SettingThemeItem|SettingsViewModel"

# ── Colores ──────────────────────────────────
RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'
CYAN='\033[0;36m'; BOLD='\033[1m'; RESET='\033[0m'

log()  { echo -e "${CYAN}[prefixsapp_log]${RESET} $*"; }
ok()   { echo -e "${GREEN}[✓]${RESET} $*"; }
warn() { echo -e "${YELLOW}[!]${RESET} $*"; }
err()  { echo -e "${RED}[✗]${RESET} $*"; exit 1; }

# ── Verificar ADB ─────────────────────────────
command -v adb >/dev/null 2>&1 || err "adb no encontrado. Instala platform-tools."

DEVICE=$(adb devices | grep -w "device" | grep -v "List" | awk '{print $1}')
[[ -z "$DEVICE" ]] && err "No hay dispositivo conectado. Verifica el cable o autorización USB."
ok "Dispositivo conectado: $(adb shell getprop ro.product.model 2>/dev/null | tr -d '\r') ($DEVICE)"

# ── Habilitar nivel VERBOSE en Honor/MagicUI ──
# Honor (MagicUI/EMUI) suprime logs de Debug/Verbose por defecto.
# persist.log.tag fuerza el nivel global para el próximo arranque,
# log.tag lo aplica en la sesión actual sin reinicio.
log "Configurando nivel de log VERBOSE para Honor..."
adb shell setprop log.tag V 2>/dev/null || true
adb shell setprop persist.log.tag V 2>/dev/null || warn "persist.log.tag requiere root — los logs pueden seguir filtrados por MagicUI"
ok "Props de log configuradas"

# ── Ampliar buffer de logcat ──────────────────
log "Ampliando buffer de logcat a 16 MB..."
adb logcat -G 16M 2>/dev/null || warn "No se pudo cambiar el buffer (requiere root en algunos dispositivos)"

# ── Limpiar buffer anterior ───────────────────
log "Limpiando buffer de logs anterior..."
adb logcat -c
ok "Buffer limpiado"

# ── Detectar variante activa ──────────────────
ACTIVE_PKG=""
ACTIVE_PID=""
for pkg in "${PACKAGES[@]}"; do
  pid=$(adb shell pidof -s "$pkg" 2>/dev/null | tr -d '\r')
  if [[ -n "$pid" ]]; then
    ACTIVE_PKG="$pkg"
    ACTIVE_PID="$pid"
    break
  fi
done

echo ""
echo -e "${BOLD}─────────────────────────────────────────────${RESET}"

# Advertir si la variante activa no es debug
if [[ -n "$ACTIVE_PKG" && "$ACTIVE_PKG" != *".debug" ]]; then
  warn "Variante activa '${ACTIVE_PKG}' NO es debug."
  warn "Timber no está plantado → los logs de la app NO aparecerán."
  warn "Instala la variante debug o agrega Timber.plant() en otras variantes."
  echo -e "${BOLD}─────────────────────────────────────────────${RESET}"
fi

if [[ -n "$ACTIVE_PKG" ]]; then
  ok "App activa: ${YELLOW}${ACTIVE_PKG}${RESET} (PID: $ACTIVE_PID)"
  echo -e "${BOLD}─────────────────────────────────────────────${RESET}"
  echo ""
  log "Iniciando logcat filtrado por PID $ACTIVE_PID (nivel D y superior)..."
  echo -e "${CYAN}  (Ctrl+C para detener)${RESET}"
  echo ""
  adb logcat --pid="$ACTIVE_PID" -v color "*:D"
else
  warn "La app no está en ejecución. Mostrando logs de tags conocidos del proyecto..."
  echo -e "${BOLD}─────────────────────────────────────────────${RESET}"
  echo ""
  log "Tags activos: ${TIMBER_TAGS//|/ | }"
  echo -e "${CYAN}  (Ctrl+C para detener)${RESET}"
  echo ""
  adb logcat -v color "*:D" | grep --line-buffered -E "$TIMBER_TAGS|AndroidRuntime|System\.err|FATAL|CRASH"
fi
