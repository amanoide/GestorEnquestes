@echo off
REM Script para ejecutar pruebas sobre el driver CtrlAnalisiDriver (WINDOWS)
REM Ejecútalo desde ENTREGA\EXE\CtrlAnalisi:
REM run_tests.bat

setlocal enabledelayedexpansion

REM Cambiar al directorio del proyecto FONTS (donde está build.gradle)
cd ..\..\FONTS

echo Ejecutando pruebas automatizadas contra CtrlAnalisiDriver...
echo.

REM Pasar las entradas al driver mediante stdin (usa more para simular heredoc)
REM Crear fichero temporal con las entradas (identicas al bash)
(
echo 502
echo noexist
echo pwd1
echo 502
echo USER_MOCK
echo wrongpwd
echo 502
echo alice
echo pwd1
echo 502
echo bob
echo pwd1
echo 502
echo carla
echo pwd1
echo 502
echo dave
echo pwd1
echo 502
echo USER_MOCK
echo 1234
echo 2
echo 502
echo alice
echo pwd1
echo 2
echo 1
echo 1
echo 1
echo 2
echo 1
echo 2
echo 502
echo bob
echo pwd1
echo 1
echo 1
echo 2
echo 2
echo 2
echo 502
echo carla
echo pwd1
echo 1
echo 1
echo 3
echo 3
echo 2
echo 1
echo 99
echo 1
echo 1
echo 2
echo 1
echo 500
echo /ruta/no/valida/enquesta.json
echo 501
echo /ruta/no/valida/resposta.json
echo 0
) | gradlew runCtrlAnalisiDriver --no-daemon --quiet -q > PROVA.txt 2>&1

echo.
echo Pruebas completadas. Revisa la salida anterior para comprobar resultados.
endlocal
