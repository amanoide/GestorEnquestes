@echo off
REM Script para ejecutar un "juego de pruebas" sobre el driver CtrlRespostaDriver
REM Ejecutalo desde ENTREGA\EXE\CtrlResposta:
REM run_tests.bat

setlocal

REM Moverse al directorio del proyecto FONTS (donde esta build.gradle)
set PROJECT_DIR=..\..\FONTS
cd /d "%PROJECT_DIR%"

echo Ejecutando pruebas automatizadas contra CtrlRespostaDriver...
echo.
echo Plan de proves (solo lectura):
echo.
echo SETUP INICIAL (realizado automaticament en init()):
echo - Usuario mock creado: USER_MOCK / 1234
echo - Encuesta mock creada con ID: 1
echo.
echo USUARIS I LOGIN:
echo 1) Crear usuario mock: alice / pwd1
echo 2) Crear usuario mock: bob / pwd1
echo 3) Login de alice / pwd1
echo 4) Logout i login como bob / pwd1
echo 5) Intento de login con credenciales incorrectas (error esperat)
echo 6) Intento de login de usuario no existente (error esperat)
echo.
echo CONTESTAR ENCUESTA:
echo 7) Alice contesta enquesta 1 todas les preguntes (text, numerica, opcions)
echo 8) Bob contesta enquesta 1 todas les preguntes con respuestas distintas
echo 9) Alice intenta contestar de nuevo (modificar)
echo 10) Intento de contestar enquesta no existent (error esperat)
echo 11) Contestar con respuesta vacia a pregunta (error esperat si esta validada)
echo.
echo MODIFICAR RESPOSTA:
echo 12) Modificar una resposta de alice (cambiar valor)
echo 13) Intento de modificar resposta de usuario distinto (error esperat - permisos)
echo 14) Intento de modificar resposta que no existeix (error esperat)
echo 15) Intento de modificar resposta de enquesta no existent (error esperat)
echo.
echo ESBORRAR RESPOSTA:
echo 16) Esborrar una resposta de bob
echo 17) Intento de esborrar resposta que no existeix (error esperat)
echo 18) Intento de esborrar resposta de usuario distinkt (error esperat)
echo 19) Intento de esborrar resposta de enquesta no existent (error esperat)
echo.
echo CONSULTAR RESPOSTES:
echo 20) Consultar respostes de enquesta 1 (veure estat final)
echo 21) Consultar respostes de enquesta no existent (error esperat)
echo.
echo IMPORTAR RESPOSTES:
echo 22) Importar respostes desde fitxer (si existe DOCS/exemple_resposta_clustering.json)
echo 23) Intento de importar desde ruta no valida (error esperat)
echo.
echo FINALIZAR:
echo 0) Sortir
echo.
echo ================================================================================
echo.

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
echo 1
echo Aquesta enquesta es molt bona
echo 50
echo 1
echo 1
echo 1
echo 1
echo Opció 1
echo 1
echo 1
echo 2
echo Aquesta enquesta es regular
echo 25
echo 2
echo 2
echo 3
echo 1
echo 1
echo Opció 3
echo 1
echo 1
echo 10
echo inexistent
echo 1
echo 502
echo alice
echo pwd1
echo 2
echo 1
echo 1_Q_text
echo Resposta modificada alice
echo 2
echo 1
echo 1_Q_text_noexist
echo Text
echo 2
echo NOEX
echo 1_Q_text
echo Text
echo 3
echo 1
echo 1_Q_text
echo 502
echo bob
echo pwd1
echo 3
echo 1
echo 1_Q_text
echo 3
echo 1_noexist
echo 1_Q_text
echo 3
echo NOEX
echo 1_Q_text
echo 5
echo 1
echo 5
echo NOEX
echo 4
echo DOCS/exemple_resposta_clustering.json
echo 4
echo /ruta/no/valida/resposta.json
echo 0
) | gradlew.bat runCtrlRespostaDriver --no-daemon --quiet -q > ..\EXE\CtrlResposta\PROVA.txt 2>&1

echo.
echo Proves completadas. Revisa la sortida anterior per comprobar resultats.
type ..\EXE\CtrlResposta\PROVA.txt

endlocal
