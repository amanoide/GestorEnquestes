@echo off
REM Script para ejecutar pruebas sobre el driver CtrlUsuariDriver (WINDOWS)
REM Ejecútalo desde ENTREGA\EXE\CtrlUsuari:
REM run_tests.bat

setlocal enabledelayedexpansion

REM Cambiar al directorio del proyecto FONTS (donde está build.gradle)
cd ..\..\FONTS

echo Ejecutando pruebas automatizadas contra CtrlUsuariDriver...
echo.

REM Crear fichero temporal con las entradas (identicas al bash)
(
echo 1
echo auto1
echo pwd1
echo 1
echo auto1
echo pwd1
echo 1

echo p
echo 2
echo auto1
echo pwd1
echo 5
echo 3
echo 2
echo auto1
echo pwd_wrong
echo 2
echo noexist
echo pwd1
echo 1
echo a
echo pwd1
echo 1
echo auto2
echo p
echo 1
echo auto2
echo pwd1
echo 2
echo auto2
echo pwd1
echo 4
echo auto2
echo 5
echo 0
) | gradlew runCtrlUsuariDriver --no-daemon --quiet -q > PROVA.txt 2>&1

echo.
echo Pruebas completadas. Revisa la salida anterior para comprobar resultados.
endlocal
