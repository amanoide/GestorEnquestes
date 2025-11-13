# ENTREGA - Estructura de Entrega del Proyecto

## Descripción General
Este directorio contiene la entrega completa del proyecto de gestión de enquestes, organizado según los requisitos especificados.

## Estructura

### 📁 DOCS
Contiene la documentación del proyecto:
- `INSTRUCCIONS_COMPILACIO_EXECUCIO.md` - Instrucciones detalladas de compilación y ejecución
- Memoria del proyecto
- Diagramas de diseño
- Especificaciones técnicas

### 📁 FONTS
Contiene el código fuente completo del proyecto:
- `src/` - Código fuente Java organizado por paquetes
- `build.gradle` - Configuración de compilación Gradle
- `settings.gradle` - Configuración del proyecto Gradle
- `gradlew` y `gradlew.bat` - Scripts de Gradle Wrapper
- `lib/` - Dependencias (GSON 2.8.9, JSON 20231013, JUnit 4.13.2)
- `gradle/` - Archivos del Gradle Wrapper

#### Compilación desde FONTS
**IMPORTANTE:** Este proyecto utiliza exclusivamente Gradle para compilación y ejecución.

```bash
# Compilar el proyecto (sin ejecutar tests)
gradlew build -x test

# Compilar incluyendo tests
gradlew build

# Limpiar y compilar
gradlew clean build -x test
```

### 📁 EXE
Contiene la documentación de prueba para cada driver, organizada en subdirectorios:

Cada subdirectorio incluye:
- `PROVA.txt` - Documentación de las pruebas del driver
- `README.md` - Descripción del driver y sus funcionalidades

#### Drivers Disponibles

1. **MainDriver** - Driver principal integrado
   - Prueba completa del sistema
   - Interfaz unificada para todas las funcionalidades

2. **CtrlDominiDriver** - Driver del controlador de dominio
   - Prueba de la fachada del sistema
   - Delegación a controladores especializados

3. **CtrlEnquestaDriver** - Driver del controlador de enquestes
   - Creación y gestión de enquestes
   - Gestión de preguntas asociadas

4. **CtrlPreguntaDriver** - Driver del controlador de preguntas
   - Creación de preguntas de diferentes tipos
   - Gestión de opciones

5. **CtrlRespostaDriver** - Driver del controlador de respostes
   - Registro de respostes
   - Consultas y estadísticas

6. **CtrlUsuariDriver** - Driver del controlador de usuarios
   - Registro e inicio de sesión
   - Gestión de información de usuario

7. **CtrlPersistenciaDriver** - Driver del controlador de persistencia
   - Guardar/cargar enquestes
   - Exportar/importar datos JSON

8. **CtrlPerfilDriver** - Driver del controlador de perfiles
   - Gestión de perfiles de usuario
   - Preferencias

9. **CtrlAnalisiDriver** - Driver del controlador de análisis
   - Análisis estadístico
   - Generación de métricas

#### Ejecución de Drivers
**IMPORTANTE:** Los drivers se ejecutan exclusivamente mediante Gradle desde el directorio FONTS.

```bash
# Desde el directorio FONTS
cd ENTREGA\FONTS

# Ejecutar driver principal
gradlew runMainDriver

# Ejecutar drivers específicos
gradlew runCtrlDominiDriver
gradlew runCtrlEnquestaDriver
gradlew runCtrlPreguntaDriver
gradlew runCtrlRespostaDriver
gradlew runCtrlUsuariDriver
gradlew runCtrlPersistenciaDriver
gradlew runCtrlPerfilDriver
gradlew runCtrlAnalisiDriver
```

Para instrucciones detalladas, consultar `DOCS/INSTRUCCIONS_COMPILACIO_EXECUCIO.md`.

## Requisitos del Sistema
- Java Development Kit (JDK) 21 o superior
- Gradle Wrapper incluido (no requiere instalación de Gradle)
- Dependencias incluidas en `lib/`: GSON 2.8.9, JSON 20231013, JUnit 4.13.2

## Arquitectura del Proyecto
El proyecto sigue una arquitectura en capas con patrón Façade:
- **MainDriver** → **CtrlDomini** (Fachada) → **Controladores especializados** → **Clases de dominio** → **CtrlPersistencia**

## Estructura de Paquetes
```
edu.upc.prop.clusterxx
├── domini
│   ├── controladors (controladores)
│   │   ├── CtrlDomini.java
│   │   ├── CtrlUsuari.java
│   │   ├── CtrlEnquesta.java
│   │   ├── CtrlPregunta.java
│   │   ├── CtrlResposta.java
│   │   ├── CtrlPersistencia.java
│   │   ├── CtrlPerfil.java
│   │   └── CtrlAnalisi.java
│   └── classes (clases de dominio)
│       ├── Usuari.java
│       ├── Enquesta.java
│       ├── Pregunta.java
│       ├── Resposta.java
│       ├── Opcio.java
│       ├── Perfil.java
│       ├── KMeans.java
│       └── Kluster.java
└── drivers (solo en FONTS/src/drivers/java/)
    └── MainDriver.java + 8 drivers de controladores
```

## Características Principales
- **Gestión de Usuarios**: Registro, autenticación y gestión de perfiles
- **Gestión de Enquestes**: Creación y administración de encuestas con múltiples tipos de preguntas
- **Análisis de Datos**: Clustering K-Means para análisis de respuestas
- **Persistencia**: Serialización JSON para guardar/cargar datos
- **Testing**: 9 drivers independientes para probar cada componente del sistema

## Compilación y Ejecución Rápida
```bash
# 1. Navegar al directorio FONTS
cd ENTREGA\FONTS

# 2. Compilar el proyecto
gradlew build -x test

# 3. Ejecutar el driver principal
gradlew runMainDriver
```

## Dependencias
- **GSON 2.8.9**: Serialización/deserialización JSON
- **JSON 20231013**: Manipulación de objetos JSON
- **JUnit 4.13.2**: Framework de testing unitario

## Notas Importantes
- ✅ El proyecto utiliza **exclusivamente Gradle** para compilación y ejecución
- ✅ No se incluyen scripts `.bat` ni `.sh` adicionales
- ✅ Todos los drivers tienen método `main()` y son ejecutables vía Gradle
- ✅ La documentación de pruebas se encuentra en archivos `PROVA.txt` dentro de cada subdirectorio de EXE

## Autor
Proyecto desarrollado para la asignatura PROP - UPC


