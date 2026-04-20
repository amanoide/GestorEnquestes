# ClusterXX — Gestor d'Enquestes amb Anàlisi de Clustering

Aplicació d'escriptori en Java per a la gestió d'enquestes amb anàlisi avançada de respostes mitjançant algorismes de clustering. Desenvolupada com a projecte de l'assignatura **PROP (Projectes de Programació)** a la **FIB – UPC** (curs 2025-2026).

## Funcionalitats principals

- **Gestió d'usuaris**: registre, autenticació i eliminació de comptes
- **Creació i gestió d'enquestes**: preguntes numèriques, ordinals, categòriques (simple/múltiple) i text lliure
- **Importació/exportació** d'enquestes i respostes en format JSON
- **Resposta a enquestes** amb validació per tipus de pregunta
- **Anàlisi de clustering**: agrupació automàtica de respostes per trobar patrons i perfils representatius
- **Visualització** de resultats d'anàlisi i perfils de clústers

## Arquitectura

El projecte segueix una **arquitectura en 3 capes**:

```
┌─────────────────────────────────┐
│       Presentació (Swing)       │  Vistes, Diàlegs, Controlador UI
├─────────────────────────────────┤
│            Domini               │  Entitats, Controladors, Clustering
├─────────────────────────────────┤
│        Persistència             │  Gestors JSON, Cache en memòria
└─────────────────────────────────┘
```

### Domini — Clustering i anàlisi (la meva contribució principal)

El nucli analític del projecte implementa un sistema complet de clustering per a **vectors heterogenis** (respostes amb tipus mixtos):

| Classe | Descripció |
|--------|------------|
| `DistanceCalculator` | Mètriques de distància per a 5 tipus de variables (numèrica, ordinal, nominal simple/múltiple, text lliure) amb normes L1 i L2 |
| `KMeans` | Algorisme K-Means clàssic amb inicialització aleatòria |
| `KMeansPlusPlus` | K-Means++ amb D² sampling per a millor qualitat de clústers |
| `KMedoids` | K-Medoids/PAM — utilitza punts reals com a centres, robust davant outliers |
| `ClusterEvaluator` | Avaluació de qualitat amb coeficient de silueta i selecció automàtica de k |
| `FeatureSpecFactory` | Conversió de metadades de preguntes a especificacions per al clustering |
| `Kluster` | Estructura de dades de clúster amb recàlcul intel·ligent del centroide |
| `CtrlAnalisi` | Controlador que coordina l'execució dels algorismes |

### Per què vectors heterogenis?

Cada resposta a una enquesta pot combinar tipus de dades molt diferents:

```
Preu (numèric): 500         → distància euclidiana normalitzada
Qualitat (ordinal): "bona"   → distància posicional
Color (nominal): "blau"      → coincidència exacta (0 o 1)
Funcions (múltiple): "wifi, càmera" → distància de Jaccard
Comentaris (text): "..."     → similitud de cadenes
```

Els algorismes de clustering estàndard no suporten aquest tipus de dades mixtes. El `DistanceCalculator` resol aquest problema amb mètriques específiques per a cada tipus de variable.

## Tecnologies

| Component | Tecnologia |
|-----------|------------|
| Llenguatge | Java 21 |
| Build | Gradle |
| Interfície | Swing |
| Dades | JSON (Gson + org.json) |
| Testing | JUnit 4 |

## Compilació i execució

```bash
# Compilar
cd FONTS
./gradlew build

# Executar
./gradlew run

# Executar des del JAR
java -jar EXE/GestorEnquestes.jar

# Generar Javadoc
./gradlew javadoc

# Executar tests
./gradlew test
```

## Estructura del projecte

```
├── FONTS/                          # Codi font
│   └── src/main/java/edu/upc/prop/clusterxx/
│       ├── Main.java              # Punt d'entrada
│       ├── domini/
│       │   ├── classes/           # Entitats + algorismes de clustering
│       │   └── controladors/      # Controladors de domini
│       ├── persistencia/          # Gestors de fitxers JSON
│       └── presentacio/           # Interfície Swing
├── DOCS/                          # Documentació i Javadoc
├── EXE/                           # JAR executable i jocs de prova
│   └── Jocs de Prova/            # 26 escenaris de test funcional
└── README.md
```

## Equip

Projecte desenvolupat per 5 membres del grup de PROP:

| Membre | Contribució principal |
|--------|----------------------|
| **Aman Kumar Aswani** | Algorismes de clustering (K-Means, K-Means++, K-Medoids), mètriques de distància heterogènia, avaluació de clústers, capa de persistència |
| Marc Fiori Porta | Entitats de domini (Usuari, Pregunta, Opcio), controladors d'enquestes i usuaris, persistència |
| Jairo Gómez Arias | Entitats d'enquesta i resposta, controladors de domini i perfils, interfície |
| Joan Garvin Cardona | Capa de presentació (Swing), diàlegs, drivers de test |
| Mohamed Dari Bachiri | Capa de persistència, tests unitaris (JUnit) |

## Documentació

La documentació Javadoc completa es troba a `DOCS/javadoc/`. L'autoria detallada de cada classe es pot consultar a `DOCS/Autoria.txt`.
