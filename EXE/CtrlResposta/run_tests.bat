@echo off
REM Script para ejecutar pruebas sobre el driver CtrlRespostaDriver (WINDOWS)
REM Ejecútalo desde ENTREGA\EXE\CtrlResposta:
REM run_tests.bat

setlocal enabledelayedexpansion

REM Cambiar al directorio del proyecto FONTS (donde está build.gradle)
cd ..\..\FONTS

echo Ejecutando pruebas automatizadas contra CtrlRespostaDriver...
echo.

REM Crear fichero temporal con las entradas (identicas al bash)
(
echo 501
echo alice
echo pwd1
echo 501
echo bob
echo pwd1
echo 502
echo alice
echo pwd1
echo 502
echo bob
echo pwd1
echo 502
echo alice
echo wrongpwd
echo 502
echo noexist
echo pwd1
echo 1
echo 1
echo Aquesta enquesta es molt bona
echo 50
echo 1
echo 1
echo 1,2
echo 1
echo 1
echo Aquesta enquesta es regular
echo 25
echo 2
echo 2
echo 3
echo 1
echo 10
echo inexistent
echo 1
echo 1
echo 1
echo 1
echo 1
echo 1
echo 2
echo 1
echo 1
echo Resposta modificada Bob
echo 2
echo NOEX
echo 6
echo 1
echo 9
echo 1
echo text nou
echo 5
echo 1
echo 3
echo 1
echo S
echo 5
echo 1
echo 5
echo NOEX
echo 1
echo 4
echo ../DOCS/exemple_resposta_clustering.json
echo 4
echo /ruta/no/valida/resposta.json
echo 500
echo ../DOCS/exemple_enquesta_clustering.json
echo 4
echo ../DOCS/exemple_resposta_clustering.json
echo 5
echo 2
echo 0
) | gradlew runCtrlRespostaDriver --no-daemon --quiet -q > ..\EXE\CtrlResposta\PROVA.txt 2>&1

echo.
echo Pruebas completadas. Revisa la salida anterior para comprobar resultados.
endlocal
