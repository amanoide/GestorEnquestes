# Sistema de Gestió d'Enquestes - PROP CLUSTER XX

## Requisits del Sistema

- **Java**: versió 21.* (obligatori)
- **JUnit**: versió 4.* (per tests)
- Sistema operatiu: Windows, Linux o macOS

## Verificar Versió de Java

Abans de compilar, assegura't que tens Java 21 instal·lat:

```bash
java -version
```

Hauria de mostrar algo com: `openjdk version "21.0.X"`

## Compilació i Execució amb Gradle

El projecte es gestiona completament amb Gradle. Assegura't d'estar a la carpeta `ENTREGA/FONTS` per executar els següents comandes.

### Comandes per a Windows
Usa `gradlew.bat`.

### Comandes per a Linux/macOS
Usa `./gradlew`.

---

### Compilar el projecte
Aquest comandament compila tot el codi font (dins ENTREGA/FONTS) i genera els arxius `.class`.
```bash

./gradlew build
```

### Executar l'aplicació principal
Executa el driver principal de l'aplicació (actualment configurat a `CtrlEnquestaDriver`).
```bash

./gradlew run
```

### Executar drivers individuals
Pots executar cada driver de manera independent amb les següents tasques:
```bash


./gradlew runCtrlEnquestaDriver
./gradlew runCtrlUsuariDriver
./gradlew runCtrlRespostaDriver
./gradlew runCtrlAnalisiDriver
```

## Arquitectura del Projecte

El projecte segueix una arquitectura en capes:
```
Drivers (Presentació)
    ↓
CtrlDomini (Controlador Façana)
    ↓
CtrlEnquesta, CtrlUsuari, CtrlResposta, etc. (Controladors específics)
    ↓
Enquesta, Usuari, Pregunta, Resposta, etc. (Domini)
    ↓
CtrlPersistencia (Persistència)
```

## Dependències

- **GSON 2.8.9**: Serialització/deserialització JSON
- **JSON 20231013**: Processament JSON
- **JUnit 4.13.1**: Framework de testing

Les dependències es descarreguen automàticamente amb Gradle.

## Solució de Problemes

### Error: "javac no se reconoce como comando"
Això indica que Java no està configurat a la variable d'entorn `PATH`. Gradle hauria de gestionar això automàticament amb el `toolchain`, però si l'error persisteix, assegura't que la variable `JAVA_HOME` apunta a la teva instal·lació de JDK 21.

### El programa no arrenca
Assegura't que has compilat el projecte abans d'intentar executar-lo. Executa `gradlew build` primer.
```bash
# Exemple complet
./gradlew build
./gradlew run
```