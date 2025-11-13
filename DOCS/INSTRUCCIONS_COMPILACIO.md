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

## Compilació i Execució

### Opció 1: Amb Gradle (recomanat per desenvolupament)

#### Compilar el projecte:
```bash
# Windows
gradlew.bat build

# Linux/Mac
./gradlew build
```

#### Executar el MainDriver:
```bash
# Windows
gradlew.bat runMainDriver

# Linux/Mac
./gradlew runMainDriver
```

#### Executar drivers individuals:
```bash
# Windows
gradlew.bat runCtrlDominiDriver
# ... etc

# Linux/Mac
./gradlew runCtrlDominiDriver
# ... etc
```


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
- **JUnit 4.13.1**: Framework de testing (versió 4.*)

Les dependències es descarreguen automàticament en executar els scripts de compilació.


✅ **Java 21.*** - Configurat i verificat
✅ **JUnit 4.*** - Versió 4.13.1 instal·lada
✅ **Compilació des de terminal** - Scripts proporcionats (compile.bat/sh)
✅ **Execució des de terminal** - Scripts proporcionats (run.bat/sh)
✅ **Sense errors de compilació** - Verificat amb `gradle build`

## Solució de Problemes

### Error: "javac no se reconoce como comando"
Java no està en el PATH. Afegeix Java 21 al PATH del sistema o utilitza la ruta completa:
```bash
"C:\Program Files\Java\jdk-21\bin\javac" ...
```

### Error de compilació amb dependències
Assegura't que les dependències s'han descarregat correctament a la carpeta `lib/`. Pots descarregar-les manualment:
- GSON: https://repo1.maven.org/maven2/com/google/code/gson/gson/2.8.9/gson-2.8.9.jar
- JSON: https://repo1.maven.org/maven2/org/json/json/20231013/json-20231013.jar

### El programa no arrenca
Verifica que has compilat abans d'executar:
```bash
# Windows
compile.bat
run.bat

# Linux/Mac
./compile.sh
./run.sh
```
