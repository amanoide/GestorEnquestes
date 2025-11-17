#!/usr/bin/env bash
# Script para ejecutar un "juego de pruebas" sobre el driver CtrlAnalisiDriver
# Ejecútalo desde ENTREGA/EXE/CtrlAnalisi:
# chmod +x run_tests.sh
# ./run_tests.sh

set -euo pipefail

# Moverse al directorio del proyecto FONTS (donde está build.gradle)
PROJECT_DIR="../../FONTS"
cd "$PROJECT_DIR"

echo "Ejecutando pruebas automatizadas contra CtrlAnalisiDriver..."

# Plan (solo para lectura). Esto se imprime en consola pero NO se envía al driver.
cat <<'PLAN'
Plan de proves (solo lectura):

SETUP INICIAL (realizado automàticament en init()):
- Usuario admin mock: USER_MOCK / 1234
- Encuesta mock creada con ID: 1 y todas las preguntes (text, numérica, cualitativa)
- Usuaris mock con respostes: alice, bob, carla, dave (pwd1 tots)

LOGIN Y MOCK SETUP:
1) Intentar login con usuari inexistent (error esperat)
2) Intentar login con password incorrect (error esperat)
3) Login exitoso de alice / pwd1
4) Cambiar a login de bob / pwd1
5) Cambiar a login de carla / pwd1
6) Cambiar a login de dave / pwd1
7) Volver a login admin USER_MOCK / 1234

ANÁLISIS SIN SUFICIENTES PARTICIPANTES:
9) Consultar perfil sin haber sido analizado aun (error esperat)

ANÁLISIS CON K MANUAL:
10) Login alice / pwd1
11) Veure meu perfil (vacio aun)
12) Analizar enquesta 1 con k manual (seleccionar k=2, algoritmo KMeans)
13) Volver a admin y veure perfil de alice (deberia tener perfil ahora)

ANÁLISIS CON K ALEATORIO:
14) Login bob / pwd1
15) Analizar enquesta 1 con k aleatorio (algoritmo KMeans++)
16) Veure meu perfil (deberia mostrar el resultado del clustering)

ANÁLISIS CON K AUTOMÁTICO (SILHOUETTE):
17) Login carla / pwd1
18) Analizar enquesta 1 con k automático (buscar óptimo, algoritmo KMedoids)
19) Veure meu perfil

INTENTOS DE ERROR:
20) Intentar analizar enquesta no existente (error esperat)
21) Importar enquesta desde ruta no valida (error esperat)
22) Importar respostes desde ruta no valida (error esperat)

FINALIZAR:
0) Sortir

NOTA: El següent bloc heredoc conté únicament les entrades (inputs)
que es passaran al driver.
PLAN

./gradlew runCtrlAnalisiDriver --no-daemon --quiet -q <<'EOF' > PROVA.txt 2>&1
502
noexist
pwd1
502
USER_MOCK
wrongpwd
502
alice
pwd1
502
bob
pwd1
502
carla
pwd1
502
dave
pwd1
502
USER_MOCK
1234
2
502
alice
pwd1
2
1
1
1
2
1
2
502
bob
pwd1
1
1
2
2
2
502
carla
pwd1
1
1
3
3
2
1
99
1
1
2
1
500
/ruta/no/valida/enquesta.json
501
/ruta/no/valida/resposta.json
0
EOF

echo "Proves completadas. Revisa la sortida anterior per comprobar resultats." 
