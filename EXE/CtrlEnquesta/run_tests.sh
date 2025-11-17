#!/usr/bin/env bash
# Script para ejecutar un "juego de pruebas" sobre el driver CtrlEnquestaDriver
# Ejecútalo desde ENTREGA/EXE/CtrlEnquesta:
# chmod +x run_tests.sh
# ./run_tests.sh

set -euo pipefail

# Moverse al directorio del proyecto FONTS (donde está build.gradle)
PROJECT_DIR="../../FONTS"
cd "$PROJECT_DIR"

echo "Ejecutando pruebas automatizadas contra CtrlEnquestaDriver..."

# Plan (solo para lectura). Esto se imprime en consola pero NO se envía al driver.
cat <<'PLAN'
Plan de proves (solo lectura):

ENQUESTES:
0) Intentar esborrar una enquesta no existent (error esperat)
1) Crear enquesta E1
2) Intentar modificar títol d'una enquesta no existent (error esperat)
3) Intentar modificar descripció d'una enquesta no existent (error esperat)
4) Modificar títol de E1
5) Modificar descripció de E1
6) Intentar crear enquesta duplicada E1 (error esperat)

PREGUNTES - CREAR:
7) Afegir pregunta TEXT a E1
8) Afegir pregunta NUMÈRICA a E1 (amb bounds inválids primer, depois valids)
9) Afegir pregunta QUALITATIVA ORDENADA a E1 (i opcions amb ordre, ordre no numèric primer)
10) Afegir pregunta QUALITATIVA SIMPLE a E1 (opcions sense ordre)
11) Afegir pregunta QUALITATIVA MÚLTIPLE a E1 (màxim seleccions + opcions)
12) Intentar afegir pregunta duplicada a E1 (error esperat)
13) Crear enquesta E2 per provar opcions amb títol/descripció vacies

PREGUNTES - MODIFICAR I ELIMINAR:
14) Intentar modificar pregunta d'una enquesta no existent (error esperat)
15) Intentar modificar pregunta que no existeix a E1 (error esperat)
16) Modificar pregunta existenta (canviar text de Q_text)
17) Intentar eliminar pregunta que no existeix a E1 (error esperat)

OPCIONS:
18) Intentar afegir opcions a pregunta NUMÈRICA (error esperat)
19) Afegir una opció manualment a pregunta QUALITATIVA ORDENADA (ordre numèric)
20) Intentar afegir opció a pregunta TEXT (error esperat)
21) Eliminar una opció existent
22) Intentar eliminar una opció no existent (error esperat)

ACCIONS FINALS:
23) Eliminar una pregunta (prova de selecció invàlida seguida d'una válida)
24) Consultar enquestes (veure estat final)
25) Intentar importar enquesta amb ruta no valida (error esperat)
26) Esborrar enquesta E1
27) Esborrar enquesta E2
28) Intentar esborrar de nou E1 (error esperat)
0) Sortir

NOTA: El següent bloc heredoc conté únicament les entrades (inputs)
que es passaran al driver.
PLAN

./gradlew runCtrlEnquestaDriver --no-daemon --quiet -q <<'EOF' > PROVA.txt 2>&1
4
NOEX
1
E1
Test Enquesta
Descripcio inicial
2
NOEX
3
NOEX
2
E1
Titol Actualitzat
3
E1
Descripcio actualitzada
1
E1
Test Enquesta
Descripcio inicial
10
E1
Q_text
Què en penses?
1
10
E1
Q_num
Quants anys tens?
2
10
5
5
120
10
E1
Q_ord
Classificació general
3
3
Bé
1
Regular
2
Mal
3
10
E1
Q_simple
Color preferit
4
3
Vermell
Verd
Blau
10
E1
Q_multi
Fruites preferides
5
2
3
Poma
Plàtan
Préssec
10
E1
Q_text
1
E2
Enquesta Buida
Sense preguntes
12
NOEX
12
E1
Q_no_exist
1
3
12
E1
1
1
Text modificat TEXT
11
E1
Q_no_exist
14
1
13
E1
4
20
13
E1
1
abc
4
13
E1
2
14
E1
3
1
14
E1
3
99
11
E1
99
4
20
5
/path/no/valida/enquesta.json
5
../DOCS/exemple_enquesta_clustering.json
20
4
E1
4
E2
4
NOEX
0
EOF

echo "Proves completades. Revisa la sortida anterior per comprobar resultats." 
