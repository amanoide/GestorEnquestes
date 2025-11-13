# Executables i Drivers del Sistema

Aquest directori conté l'estructura per executar els diferents drivers de prova del sistema de gestió d'enquestes.

## Estructura

Cada subdirectori conté:
- **PROVA.txt**: Documentació completa de la prova (objectius, valors provats, operativa)

## Drivers Disponibles

### MainDriver
Driver principal integrat que prova el sistema complet.
- Ubicació: `MainDriver/PROVA.txt`
- Execució: `cd FONTS && gradlew.bat runMainDriver`

### CtrlDomini
Prova del controlador façana principal.
- Ubicació: `CtrlDomini/PROVA.txt`
- Execució: `cd FONTS && gradlew.bat runCtrlDominiDriver`

### CtrlEnquesta
Prova del controlador de gestió d'enquestes.
- Ubicació: `CtrlEnquesta/PROVA.txt`
- Execució: `cd FONTS && gradlew.bat runCtrlEnquestaDriver`

### CtrlPregunta
Prova del controlador de preguntes.
- Ubicació: `CtrlPregunta/PROVA.txt`
- Execució: `cd FONTS && gradlew.bat runCtrlPreguntaDriver`

### CtrlResposta
Prova del controlador de respostes.
- Ubicació: `CtrlResposta/PROVA.txt`
- Execució: `cd FONTS && gradlew.bat runCtrlRespostaDriver`

### CtrlUsuari
Prova del controlador d'usuaris i sessions.
- Ubicació: `CtrlUsuari/PROVA.txt`
- Execució: `cd FONTS && gradlew.bat runCtrlUsuariDriver`

### CtrlPersistencia
Prova del controlador de persistència (guardar/carregar dades).
- Ubicació: `CtrlPersistencia/PROVA.txt`
- Execució: `cd FONTS && gradlew.bat runCtrlPersistenciaDriver`

### CtrlPerfil
Prova del controlador de perfils d'usuari.
- Ubicació: `CtrlPerfil/PROVA.txt`
- Execució: `cd FONTS && gradlew.bat runCtrlPerfilDriver`

### CtrlAnalisi
Prova del controlador d'anàlisi estadístic.
- Ubicació: `CtrlAnalisi/PROVA.txt`
- Execució: `cd FONTS && gradlew.bat runCtrlAnalisiDriver`

## Com executar els drivers

Tots els drivers s'executen únicament amb Gradle des del directori FONTS.

### Executar un driver

```bash
cd FONTS

# Veure tots els drivers disponibles
gradlew.bat tasks --group=drivers

# Executar un driver específic
gradlew.bat runMainDriver
gradlew.bat runCtrlDominiDriver
gradlew.bat runCtrlEnquestaDriver
gradlew.bat runCtrlPreguntaDriver
gradlew.bat runCtrlRespostaDriver
gradlew.bat runCtrlUsuariDriver
gradlew.bat runCtrlPersistenciaDriver
gradlew.bat runCtrlPerfilDriver
gradlew.bat runCtrlAnalisiDriver
```

### Linux/Mac

```bash
cd FONTS
./gradlew runMainDriver
# etc...
```

## Classes compilades

Les classes compilades es troben a:
- `main/`: Classes del domini i controladors
- `drivers/`: Classes dels drivers de prova
- `test/`: Classes de tests unitaris
- `lib/`: Dependències (GSON, JSON)

## Documentació completa

Per instruccions detallades de compilació i execució, consulteu:
`../DOCS/INSTRUCCIONS_COMPILACIO_EXECUCIO.md`
