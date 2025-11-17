#!/usr/bin/env bash
# Script para ejecutar un "juego de pruebas" sobre el driver CtrlRespostaDriver
# Ejecútalo desde ENTREGA/EXE/CtrlResposta:
# chmod +x run_tests.sh
# ./run_tests.sh

set -euo pipefail

# Moverse al directorio del proyecto FONTS (donde está build.gradle)
PROJECT_DIR="../../FONTS"
cd "$PROJECT_DIR"

echo "Ejecutando pruebas automatizadas contra CtrlRespostaDriver..."

# Plan (solo para lectura). Esto se imprime en consola pero NO se envía al driver.
cat <<'PLAN'
Plan de proves (solo lectura):

SETUP INICIAL (realizado automàticament en init()):
- Usuario mock creado: USER_MOCK / 1234
- Encuesta mock creada con ID: 1

USUARIS I LOGIN:
1) Crear usuario mock: alice / pwd1
2) Crear usuario mock: bob / pwd1
3) Login de alice / pwd1
4) Logout i login como bob / pwd1
5) Intento de login con credenciales incorrectas (error esperat)
6) Intento de login de usuario no existente (error esperat)

CONTESTAR ENCUESTA:
7) Bob contesta enquesta 1 todas les preguntes (text, numérica, opcions)
8) Bob contesta enquesta 1 todas les preguntes con respuestas distintas(error esperat - ya contestada)
9) Alice intenta contestar de nuevo (modificar)
10) Intento de contestar enquesta no existent (error esperat)

MODIFICAR RESPOSTA:
11) Modificar una resposta de Bob (cambiar valor)
12) Intento de modificar resposta que no existeix (error esperat)

ESBORRAR RESPOSTA:
13) Esborrar una resposta de bob

CONSULTAR RESPOSTES:
14) Consultar respostes de enquesta 1 (veure estat final)
15) Consultar respostes de enquesta no existent (error esperat)

IMPORTAR RESPOSTES:
16) Importar respostes desde fitxer a enquesta no existent(si existe DOCS/exemple_resposta_clustering.json)
17) Intento de importar desde ruta no valida (error esperat)
18) Importar respostes desde fitxer a enquesta existent (si existe DOCS/exemple_resposta_clustering.json)

FINALIZAR:
0) Sortir

NOTA: El següent bloc heredoc conté únicament les entrades (inputs)
que es passaran al driver.
PLAN

./gradlew runCtrlRespostaDriver --no-daemon --quiet -q <<'EOF' > PROVA.txt 2>&1
501
alice
pwd1
501
bob
pwd1
502
alice
pwd1
502
bob
pwd1
502
alice
wrongpwd
502
noexist
pwd1
1
1
Aquesta enquesta es molt bona
50
1
1
1,2
1
1
Aquesta enquesta es regular
25
2
2
3
1
10
inexistent
1
1
1
1
1
1
2
1
1
Resposta modificada Bob
2
NOEX
6
1
9
1
text nou
5
1
3
1
S
5
1
5
NOEX
1
4
../DOCS/exemple_resposta_clustering.json
4
/ruta/no/valida/resposta.json
500
../DOCS/exemple_enquesta_clustering.json
4
../DOCS/exemple_resposta_clustering.json
5
2
0
EOF

echo "Proves completadas. Revisa la sortida anterior per comprobar resultats."
