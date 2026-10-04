#!/usr/bin/env bash
# Copia a libs/ todos los .jar de la carpeta libraries del servidor (menos cache/).
# Uso: tools/libs.sh <carpeta servidor>
set -euo pipefail
cd "$(dirname "$0")/.."
SRC="${1:?carpeta del servidor}"
mkdir -p libs
find "$SRC" -name '*.jar' -not -path '*/cache/*' -exec cp {} libs/ \;
ls libs | wc -l
