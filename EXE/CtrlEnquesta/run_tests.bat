@echo off
REM Script para ejecutar pruebas sobre el driver CtrlEnquestaDriver (WINDOWS)
REM Ejecútalo desde ENTREGA\EXE\CtrlEnquesta:
REM run_tests.bat

setlocal enabledelayedexpansion

REM Cambiar al directorio del proyecto FONTS (donde está build.gradle)
cd ..\..\FONTS

echo Ejecutando pruebas automatizadas contra CtrlEnquestaDriver...
echo.

REM Crear fichero temporal con las entradas
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
) | gradlew runCtrlEnquestaDriver --no-daemon --quiet -q

echo.
echo Pruebas completadas. Revisa la salida anterior para comprobar resultados.
endlocal
