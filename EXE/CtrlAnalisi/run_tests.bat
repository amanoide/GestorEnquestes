@echo off
REM Script para ejecutar un "juego de pruebas" sobre el driver CtrlAnalisiDriver
REM Ejecutalo desde ENTREGA\EXE\CtrlAnalisi:
REM run_tests.bat

setlocal

REM Moverse al directorio del proyecto FONTS (donde esta build.gradle)
set PROJECT_DIR=..\..\FONTS
cd /d "%PROJECT_DIR%"

echo Ejecutando pruebas automatizadas contra CtrlAnalisiDriver...
echo.
echo Plan de proves (solo lectura):
echo.
echo SETUP INICIAL (realizado automaticament en init()):
echo - Usuario admin mock: USER_MOCK / 1234
echo - Encuesta mock creada con ID: 1 y todas las preguntes (text, numerica, cualitativa)
echo - Usuaris mock con respostes: alice, bob, carla, dave (pwd1 tots)
echo.
echo LOGIN Y MOCK SETUP:
echo 1) Intentar login con usuari inexistent (error esperat)
echo 2) Intentar login con password incorrect (error esperat)
echo 3) Login exitoso de alice / pwd1
echo 4) Cambiar a login de bob / pwd1
echo 5) Cambiar a login de carla / pwd1
echo 6) Cambiar a login de dave / pwd1
echo 7) Volver a login admin USER_MOCK / 1234
echo.
echo ANALISIS SIN SUFICIENTES PARTICIPANTES:
echo 8) Analizar enquesta cuando solo hay 1 participant (error esperat - necesita ^>= 2)
echo 9) Consultar perfil sin haber sido analizado aun (error esperat)
echo.
echo ANALISIS CON K MANUAL:
echo 10) Login alice / pwd1
echo 11) Veure meu perfil (vacio aun)
echo 12) Analizar enquesta 1 con k manual (seleccionar k=2, algoritmo KMeans)
echo 13) Volver a admin y veure perfil de alice (deberia tener perfil ahora)
echo.
echo ANALISIS CON K ALEATORIO:
echo 14) Login bob / pwd1
echo 15) Analizar enquesta 1 con k aleatorio (algoritmo KMeans++)
echo 16) Veure meu perfil (deberia mostrar el resultado del clustering)
echo.
echo ANALISIS CON K AUTOMATICO (SILHOUETTE):
echo 17) Login carla / pwd1
echo 18) Analizar enquesta 1 con k automatico (buscar optimo, algoritmo KMedoids)
echo 19) Veure meu perfil
echo.
echo INTENTOS DE ERROR:
echo 20) Intentar analizar enquesta no existente (error esperat)
echo 21) Importar enquesta desde ruta no valida (error esperat)
echo 22) Importar respostes desde ruta no valida (error esperat)
echo.
echo FINALIZAR:
echo 0) Sortir
echo.
echo ================================================================================
echo.

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
echo 1
echo 1
echo 1
echo 2
echo 1
echo 1
echo 1
echo 2
echo 2
echo 502
echo alice
echo pwd1
echo 2
echo 1
echo 1
echo 2
echo 1
echo 1
echo 2
echo 502
echo USER_MOCK
echo 1234
echo 2
echo 1
echo 502
echo bob
echo pwd1
echo 1
echo 1
echo 2
echo 2
echo 2
echo 1
echo 2
echo 502
echo carla
echo pwd1
echo 1
echo 1
echo 3
echo 2
echo 3
echo 1
echo 2
echo 1
echo 500
echo /ruta/no/valida/enquesta.json
echo 501
echo /ruta/no/valida/resposta.json
echo 0
) | gradlew.bat runCtrlAnalisiDriver --no-daemon --quiet -q > ..\EXE\CtrlAnalisi\PROVA.txt 2>&1

echo.
echo Proves completadas. Revisa la sortida anterior per comprobar resultats.
type ..\EXE\CtrlAnalisi\PROVA.txt

endlocal
