@echo off
REM Script para ejecutar un "juego de pruebas" sobre el driver CtrlUsuariDriver
REM Ejecutalo desde ENTREGA\EXE\CtrlUsuari:
REM run_tests.bat

setlocal

REM Moverse al directorio del proyecto FONTS (donde esta build.gradle)
set PROJECT_DIR=..\..\FONTS
cd /d "%PROJECT_DIR%"

echo Ejecutando pruebas automatizadas contra CtrlUsuariDriver...
echo.
echo Plan de pruebas (solo lectura):
echo.
echo CREAR Y REGISTRAR USUARIOS:
echo 1) Registrar usuario valido: auto1 / pwd1
echo 2) Intento de registrar duplicado: auto1 / pwd1
echo 3) Intento de registrar invalido (username vacio, pwd corta)
echo.
echo LOGIN Y LOGOUT:
echo 4) Login correcto: auto1 / pwd1
echo 5) Consultar usuario actual (deberia ser auto1)
echo 6) Logout de auto1
echo.
echo LOGIN CON ERRORES:
echo 7) Login con contrasena incorrecta (error esperat)
echo 8) Login con usuario inexistente (error esperat)
echo 9) Intento de registrar con username muy corto "a" (error esperat)
echo 10) Intento de registrar con contrasena muy corta "p" (error esperat)
echo.
echo MAS USUARIOS:
echo 11) Registrar otro usuario valido: auto2 / pwd1
echo 12) Login correcto con auto2
echo 13) Borrar auto2 mientras esta logueado (deberia funcionar)
echo 14) Consultar usuario actual final
echo.
echo FINALIZAR:
echo 0) Sortir
echo.
echo ================================================================================
echo.

(
echo 1
echo auto1
echo pwd1
echo 1
echo auto1
echo pwd1
echo 1
echo.
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
) | gradlew.bat runCtrlUsuariDriver --no-daemon --quiet -q > ..\EXE\CtrlUsuari\PROVA.txt 2>&1

echo.
echo Pruebas completadas. Revisa la salida anterior para comprobar resultados.
type ..\EXE\CtrlUsuari\PROVA.txt

endlocal
