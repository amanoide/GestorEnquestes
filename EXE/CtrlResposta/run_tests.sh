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
7) Alice contesta enquesta 1 todas les preguntes (text, numérica, opcions)
8) Bob contesta enquesta 1 todas les preguntes con respuestas distintas
9) Alice intenta contestar de nuevo (modificar)
10) Intento de contestar enquesta no existent (error esperat)
11) Contestar con respuesta vacía a pregunta (error esperat si está validada)

MODIFICAR RESPOSTA:
12) Modificar una resposta de alice (cambiar valor)
13) Intento de modificar resposta de usuario distinto (error esperat - permisos)
14) Intento de modificar resposta que no existeix (error esperat)
15) Intento de modificar resposta de enquesta no existent (error esperat)

ESBORRAR RESPOSTA:
16) Esborrar una resposta de bob
17) Intento de esborrar resposta que no existeix (error esperat)
18) Intento de esborrar resposta de usuario distinkt (error esperat)
19) Intento de esborrar resposta de enquesta no existent (error esperat)

CONSULTAR RESPOSTES:
20) Consultar respostes de enquesta 1 (veure estat final)
21) Consultar respostes de enquesta no existent (error esperat)

IMPORTAR RESPOSTES:
22) Importar respostes desde fitxer (si existe DOCS/exemple_resposta_clustering.json)
23) Intento de importar desde ruta no valida (error esperat)

FINALIZAR:
0) Sortir

NOTA: El següent bloc heredoc conté únicament les entrades (inputs)
que es passaran al driver.
PLAN

./gradlew runCtrlRespostaDriver --no-daemon --quiet -q <<'EOF'
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
1
Aquesta enquesta es molt bona
50
1
1
1
1
Opció 1
1
1
2
Aquesta enquesta es regular
25
2
2
3
1
1
Opció 3
1
1
10
inexistent
1
502
alice
pwd1
2
1
1_Q_text
Resposta modificada alice
2
1
1_Q_text_noexist
Text
2
NOEX
1_Q_text
Text
3
1
1_Q_text
502
bob
pwd1
3
1
1_Q_text
3
1_noexist
1_Q_text
3
NOEX
1_Q_text
5
1
5
NOEX
4
DOCS/exemple_resposta_clustering.json
4
/ruta/no/valida/resposta.json
0
EOF

echo "Proves completadas. Revisa la sortida anterior per comprobar resultats." 
