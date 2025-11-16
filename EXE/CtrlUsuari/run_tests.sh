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
4) Registrar usuario válido: auto2 / pwd1

LOGIN Y LOGOUT:
5) Login correcto: auto1 / pwd1
6) Consultar usuario actual (debería ser auto1)
7) Logout de auto1
8) Intento de logout sin estar logueado (error esperat)

LOGIN CON ERRORES:
9) Login con contraseña incorrecta (error esperat)
10) Login con usuario inexistente (error esperat)
11) Intento de registrar con username muy corto "a" (error esperat)

MÁS USUARIOS:
12) Registrar otro usuario válido: auto3 / pwd1
13) Login correcto con auto3
14) Intento de borrar usuario sin estar logueado (debería fallar)
15) Borrar auto3 mientras está logueado (debería funcionar)
16) Consultar usuario actual final

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
4
auto1
wrongpwd
2
noexist
pwd1
1
a
pwd1
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
