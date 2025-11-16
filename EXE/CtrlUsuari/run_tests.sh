#!/usr/bin/env bash
# Script para ejecutar un "juego de pruebas" sobre el driver CtrlUsuariDriver
# Ejecútalo desde ENTREGA/EXE/CtrlUsuari:
# chmod +x run_tests.sh
# ./run_tests.sh

set -euo pipefail

# Moverse al directorio del proyecto FONTS (donde está build.gradle)
PROJECT_DIR="../../FONTS"
cd "$PROJECT_DIR"

echo "Ejecutando pruebas automatizadas contra CtrlUsuariDriver..."

# Plan (solo para lectura). Esto se imprime en consola pero NO se envía al driver.
cat <<'PLAN'
Plan de pruebas (solo lectura):

CREAR Y REGISTRAR USUARIOS:
1) Registrar usuario válido: auto1 / pwd1
2) Intento de registrar duplicado: auto1 / pwd1
3) Intento de registrar inválido (username vacío, pwd corta)

LOGIN Y LOGOUT:
4) Login correcto: auto1 / pwd1
5) Consultar usuario actual (debería ser auto1)
6) Logout de auto1

LOGIN CON ERRORES:
7) Login con contraseña incorrecta (error esperat)
8) Login con usuario inexistente (error esperat)
9) Intento de registrar con username muy corto "a" (error esperat)
10) Intento de registrar con contraseña muy corta "p" (error esperat)

MÁS USUARIOS:
11) Registrar otro usuario válido: auto2 / pwd1
12) Login correcto con auto2
13) Borrar auto2 mientras está logueado (debería funcionar)
14) Consultar usuario actual final

FINALIZAR:
0) Sortir

NOTA: El siguiente bloque heredoc contiene únicamente las entradas (inputs)
que se enviarán al driver.
PLAN

./gradlew runCtrlUsuariDriver --no-daemon --quiet -q <<'EOF'
1
auto1
pwd1
1
auto1
pwd1
1

p
2
auto1
pwd1
5
3
2
auto1
pwd_wrong
2
noexist
pwd1
1
a
pwd1
1
auto2
p
1
auto2
pwd1
2
auto2
pwd1
4
auto2
5
0
EOF

echo "Pruebas completadas. Revisa la salida anterior para comprobar resultados."
