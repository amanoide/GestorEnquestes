@echo off
REM Script para ejecutar un "juego de pruebas" sobre el driver CtrlEnquestaDriver
REM Ejecutalo desde ENTREGA\EXE\CtrlEnquesta:
REM run_tests.bat

setlocal

REM Moverse al directorio del proyecto FONTS (donde esta build.gradle)
set PROJECT_DIR=..\..\FONTS
cd /d "%PROJECT_DIR%"

echo Ejecutando pruebas automatizadas contra CtrlEnquestaDriver...
echo.
echo Plan de proves (solo lectura):
echo.
echo ENQUESTES:
echo 0) Intentar esborrar una enquesta no existent (error esperat)
echo 1) Crear enquesta E1
echo 2) Intentar modificar titol d'una enquesta no existent (error esperat)
echo 3) Intentar modificar descripcio d'una enquesta no existent (error esperat)
echo 4) Modificar titol de E1
echo 5) Modificar descripcio de E1
echo 6) Intentar crear enquesta duplicada E1 (error esperat)
echo.
echo PREGUNTES - CREAR:
echo 7) Afegir pregunta TEXT a E1
echo 8) Afegir pregunta NUMERICA a E1 (amb bounds invalids primer, depois valids)
echo 9) Afegir pregunta QUALITATIVA ORDENADA a E1 (i opcions amb ordre, ordre no numeric primer)
echo 10) Afegir pregunta QUALITATIVA SIMPLE a E1 (opcions sense ordre)
echo 11) Afegir pregunta QUALITATIVA MULTIPLE a E1 (maxim seleccions + opcions)
echo 12) Intentar afegir pregunta duplicada a E1 (error esperat)
echo 13) Crear enquesta E2 per provar opcions amb titol/descripcio vacies
echo.
echo PREGUNTES - MODIFICAR I ELIMINAR:
echo 14) Intentar modificar pregunta d'una enquesta no existent (error esperat)
echo 15) Intentar modificar pregunta que no existeix a E1 (error esperat)
echo 16) Modificar pregunta existenta (canviar text de Q_text)
echo 17) Intentar eliminar pregunta que no existeix a E1 (error esperat)
echo.
echo OPCIONS:
echo 18) Intentar afegir opcions a pregunta NUMERICA (error esperat)
echo 19) Afegir una opcio manualment a pregunta QUALITATIVA ORDENADA (ordre numeric)
echo 20) Intentar afegir opcio a pregunta TEXT (error esperat)
echo 21) Eliminar una opcio existent
echo 22) Intentar eliminar una opcio no existent (error esperat)
echo.
echo ACCIONS FINALS:
echo 23) Eliminar una pregunta (prova de seleccio invalida seguida d'una valida)
echo 24) Consultar enquestes (veure estat final)
echo 25) Intentar importar enquesta amb ruta no valida (error esperat)
echo 26) Esborrar enquesta E1
echo 27) Esborrar enquesta E2
echo 28) Intentar esborrar de nou E1 (error esperat)
echo 0) Sortir
echo.
echo ================================================================================
echo.

(
echo 4
echo NOEX
echo 1
echo E1
echo Test Enquesta
echo Descripcio inicial
echo 2
echo NOEX
echo 3
echo NOEX
echo 2
echo E1
echo Titol Actualitzat
echo 3
echo E1
echo Descripcio actualitzada
echo 1
echo E1
echo Test Enquesta
echo Descripcio inicial
echo 10
echo E1
echo Q_text
echo Què en penses?
echo 1
echo 10
echo E1
echo Q_num
echo Quants anys tens?
echo 2
echo 10
echo 5
echo 5
echo 120
echo 10
echo E1
echo Q_ord
echo Classificació general
echo 3
echo 3
echo Bé
echo 1
echo Regular
echo 2
echo Mal
echo 3
echo 10
echo E1
echo Q_simple
echo Color preferit
echo 4
echo 3
echo Vermell
echo Verd
echo Blau
echo 10
echo E1
echo Q_multi
echo Fruites preferides
echo 5
echo 2
echo 3
echo Poma
echo Plàtan
echo Préssec
echo 10
echo E1
echo Q_text
echo 1
echo E2
echo Enquesta Buida
echo Sense preguntes
echo 12
echo NOEX
echo 12
echo E1
echo Q_no_exist
echo 1
echo 3
echo 12
echo E1
echo 1
echo 1
echo Text modificat TEXT
echo 11
echo E1
echo Q_no_exist
echo 14
echo 1
echo 13
echo E1
echo 4
echo 20
echo 13
echo E1
echo 1
echo abc
echo 4
echo 13
echo E1
echo 2
echo 14
echo E1
echo 3
echo 1
echo 14
echo E1
echo 3
echo 99
echo 11
echo E1
echo 99
echo 4
echo 20
echo 5
echo /path/no/valida/enquesta.json
echo 5
echo ../DOCS/exemple_enquesta_clustering.json
echo 20
echo 4
echo E1
echo 4
echo E2
echo 4
echo NOEX
echo 0
) | gradlew.bat runCtrlEnquestaDriver --no-daemon --quiet -q > ..\EXE\CtrlEnquesta\PROVA.txt 2>&1

echo.
echo Proves completades. Revisa la sortida anterior per comprobar resultats.
type ..\EXE\CtrlEnquesta\PROVA.txt

endlocal
