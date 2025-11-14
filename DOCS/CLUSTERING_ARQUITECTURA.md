# Integración del Sistema de Clustering - Arquitectura

## 📋 Resumen
Este documento describe la integración completa del sistema de clustering (análisis de usuarios por respuestas) siguiendo la arquitectura MVC del proyecto. El sistema permite agrupar usuarios según sus patrones de respuesta en las encuestas y asignarles perfiles automáticamente.

---

## 🏗️ Arquitectura del Sistema

### Flujo de Datos
```
Usuario (MainDriver)
    ↓
CtrlDomini (Facade/Coordinador)
    ↓
CtrlAnalisi (Lógica de Clustering)
    ↓
Algoritmos (KMeans/KMeans++)
    ↓
Evaluación (ClusterEvaluator)
    ↓
Persistencia (CtrlPersistencia)
```

### Principios de Diseño
1. **Separación de responsabilidades**: Cada capa tiene una función específica
2. **Facade Pattern**: CtrlDomini actúa como punto único de entrada
3. **Delegación**: MainDriver delega toda la lógica de negocio
4. **Persistencia automática**: Los datos se guardan sin intervención manual

---

## 📁 Archivos Modificados

### 1. **CtrlDomini.java** (`src/main/java/edu/upc/prop/clusterxx/domini/controladors/`)

#### Cambios realizados:

**a) Imports añadidos:**
```java
import java.util.List;
import edu.upc.prop.clusterxx.domini.classes.Kluster;
import edu.upc.prop.clusterxx.domini.classes.DistanceCalculator;
import edu.upc.prop.clusterxx.domini.classes.ClusterEvaluator;
```

**b) Campo nuevo:**
```java
private CtrlAnalisi ctrlAnalisi; // Controlador de clustering
```
Inicializado en el constructor:
```java
this.ctrlAnalisi = new CtrlAnalisi();
```

**c) Método principal: `analitzarEnquesta()`**
```java
public ResultatClustering analitzarEnquesta(String idEnquesta, int k, 
    boolean usePlusPlus, int maxIters, String algoritmeNom)
```

**Responsabilidades:**
- ✅ Validar que la enquesta existe
- ✅ Obtener y validar las preguntas
- ✅ Vectorizar las respuestas de todos los usuarios
- ✅ Construir FeatureSpec[] desde las preguntas (metadatos para comparación)
- ✅ Ejecutar clustering delegando a CtrlAnalisi
- ✅ Calcular métricas de calidad (Silhouette)
- ✅ Generar nombres descriptivos para clusters
- ✅ Crear objetos Perfil para cada cluster
- ✅ Asignar perfiles a usuarios automáticamente
- ✅ Retornar ResultatClustering con todos los datos

**d) Método auxiliar: `generarNomCluster()`**
```java
private String generarNomCluster(int index, String[] centroid, List<Pregunta> preguntes)
```

**Estrategia de nombrado:**
- Analiza el centroide (respuesta típica del cluster)
- Busca la primera pregunta significativa
- Para preguntas numéricas: "Grup Baix/Mitjà/Alt"
- Para preguntas cualitativas: usa el valor predominante
- Por defecto: "Cluster N"

**e) Clase interna: `ResultatClustering`**
```java
public static class ResultatClustering {
    public final List<Kluster> clusters;
    public final double silhouetteGlobal;
    public final double[] silhouettePerCluster;
    public final List<String> usernames;
    public final List<String[]> dataVectors;
}
```

**Propósito:** Encapsular todos los resultados del análisis para retornarlos de forma estructurada.

---

### 2. **MainDriver.java** (`src/drivers/java/edu/upc/prop/clusterxx/`)

#### Cambios realizados:

**a) Import eliminado:**
```java
// ELIMINADO: import edu.upc.prop.clusterxx.domini.controladors.CtrlAnalisi;
```
Ya no se accede directamente al controlador de análisis.

**b) Menú actualizado:**
```java
System.out.println("│ 11. Analitzar enquesta (Clustering)        │");
System.out.println("│ 12. Veure el meu perfil                    │");  // NUEVO
```

**c) Case añadido en switch:**
```java
case "12":
    veureMeuPerfil();
    break;
```

**d) Método `analitzarEnquesta()` refactorizado:**

**ANTES (código complejo, ~200 líneas):**
- Creaba instancia de CtrlAnalisi directamente
- Vectorizaba respuestas localmente
- Ejecutaba clustering
- Evaluaba calidad
- Creaba y asignaba perfiles manualmente
- Generaba nombres de clusters

**DESPUÉS (código simple, ~90 líneas):**
```java
// Solo maneja UI y delegación
CtrlDomini.ResultatClustering resultat = ctrlDomini.analitzarEnquesta(
    enquesta.getId(), k, usePlusPlus, 100, algoritmeNom
);

// Muestra resultados desde el objeto retornado
System.out.println("📊 Coeficient Silhouette: " + resultat.silhouetteGlobal);
// ... más visualización
```

**Responsabilidades actuales de MainDriver:**
- ✅ Mostrar menú de enquestes
- ✅ Solicitar input del usuario (k, algoritmo)
- ✅ Llamar a `ctrlDomini.analitzarEnquesta()`
- ✅ Mostrar resultados de forma visual
- ❌ **NO** maneja lógica de clustering
- ❌ **NO** accede a CtrlAnalisi directamente
- ❌ **NO** crea perfiles manualmente

**e) Método `generarNomCluster()` eliminado:**
Movido a CtrlDomini donde corresponde (lógica de negocio).

**f) Método nuevo: `veureMeuPerfil()`**
```java
private static void veureMeuPerfil() {
    HashMap<String, Perfil> perfils = usuariActual.getPerfils();
    
    if (perfils.isEmpty()) {
        System.out.println("⚠ Encara no tens cap perfil assignat.");
        return;
    }
    
    for (Map.Entry<String, Perfil> entry : perfils.entrySet()) {
        Perfil perfil = entry.getValue();
        System.out.println(perfil.getPerfilLlegible());
    }
}
```

**Propósito:** Mostrar todos los perfiles asignados al usuario actual.

---

### 3. **Perfil.java** (`src/main/java/edu/upc/prop/clusterxx/domini/classes/`)

#### Modificaciones previas documentadas:

**Campos añadidos:**
```java
private String idEnquesta;           // Enquesta analitzada
private int clusterIndex;            // Índex del cluster (0-based)
private String clusterNom;           // Nom descriptiu del cluster
private int clusterMida;             // Nombre de membres
private double clusterSilhouette;    // Qualitat del cluster
private String[] vectorCaracteristic; // Centroide del cluster
private List<String> nomsPreguntes;  // Textos de les preguntes
private String algoritme;            // "KMeans" o "KMeans++"
private long timestamp;              // Moment de creació
```

**Métodos añadidos:**
- `teClustering()`: Verifica si el perfil tiene datos de clustering
- `getPerfilLlegible()`: Genera representación textual detallada
- `getQualitatText()`: Interpreta el coeficiente Silhouette
- Constructor completo con 10 parámetros

---

### 4. **Usuari.java** (`src/main/java/edu/upc/prop/clusterxx/domini/classes/`)

#### Modificaciones previas documentadas:

**Campo añadido:**
```java
private HashMap<String, Perfil> perfils; // Key: idEnquesta, Value: Perfil
```

**Métodos añadidos:**
- `assignarPerfil(String idEnquesta, Perfil perfil)`: Asigna un perfil al usuario
- `getPerfil(String idEnquesta)`: Obtiene perfil de una enquesta específica
- `getPerfils()`: Obtiene todos los perfiles
- `tePerfil(String idEnquesta)`: Verifica si tiene perfil para una enquesta
- `eliminarPerfil(String idEnquesta)`: Elimina un perfil

---

## 🔄 Flujo Completo de Ejecución

### Fase 1: Usuario inicia clustering
1. Usuario selecciona opción "11. Analitzar enquesta"
2. MainDriver muestra lista de enquestes
3. Usuario selecciona enquesta, k (número de clusters) y algoritmo
4. MainDriver llama a `ctrlDomini.analitzarEnquesta()`

### Fase 2: CtrlDomini coordina el análisis
5. CtrlDomini obtiene la enquesta desde CtrlEnquesta
6. Obtiene todas las respuestas desde CtrlResposta
7. **Vectoriza respuestas**: Convierte HashMap<username, Resposta> → List<String[]>
8. **Construye FeatureSpec[]**: Metadatos de cada pregunta (tipo, rango, opciones)
9. Delega clustering a CtrlAnalisi

### Fase 3: CtrlAnalisi ejecuta algoritmos
10. CtrlAnalisi llama a KMeans o KMeans++
11. Los algoritmos usan DistanceCalculator con FeatureSpec para calcular distancias
12. Retorna List<Kluster> con centroides y miembros

### Fase 4: Evaluación de calidad
13. CtrlDomini crea ClusterEvaluator
14. Calcula Silhouette global y por cluster
15. Valores: [-1, 1] donde >0.7 = excelente, 0.5-0.7 = bueno, etc.

### Fase 5: Creación y asignación de perfiles
16. Para cada cluster:
    - Genera nombre descriptivo (analiza centroide)
    - Crea objeto Perfil con todos los datos
    - **Asigna perfil a cada usuario del cluster** via `usuari.assignarPerfil()`
17. Los usuarios ya están en CtrlPersistencia → perfiles se persisten automáticamente

### Fase 6: Retorno y visualización
18. CtrlDomini retorna ResultatClustering
19. MainDriver muestra resultados:
    - Silhouette global e interpretación
    - Cada cluster con miembros y centroide
    - Mensaje de confirmación
20. Usuario puede ver su perfil con opción "12. Veure el meu perfil"

---

## 📊 Ejemplo de Uso

### Input del usuario:
```
Enquesta: "Preferències d'estudi" (6 participants)
K: 2 clusters
Algoritme: KMeans++
```

### Proceso interno:
```
Vectorització:
  user1 → ["25", "Molt d'acord", "Python,Java"]
  user2 → ["10", "En desacord", "C++"]
  ...

FeatureSpec:
  [0] → NUMERIC (min=0, max=100)
  [1] → ORDINAL (ordre: En desacord < ... < Molt d'acord)
  [2] → NOMINAL_MULTI (opcions: Python, Java, C++, JavaScript)

Clustering:
  Cluster 0: user1, user3, user5 → Centroid: ["22", "D'acord", "Python"]
  Cluster 1: user2, user4, user6 → Centroid: ["8", "En desacord", "C++"]

Silhouette: 0.65 → "Bona qualitat"

Perfils creats i assignats:
  user1.perfils["enq_001"] → Perfil(cluster=0, nom="Grup Alt", ...)
  user2.perfils["enq_001"] → Perfil(cluster=1, nom="Grup Baix", ...)
```

### Output visual:
```
╔══════════════════════════════════════════════╗
║      RESULTATS DEL CLUSTERING               ║
╚══════════════════════════════════════════════╝

📊 Algoritme: KMeans++
📊 Nombre de clusters: 2
📊 Participants analitzats: 6
📊 Coeficient Silhouette global: 0.650
   Qualitat: Bona ✓✓ (estructura de clusters clara)

┌─ CLUSTER 1 ─┐
│ Mida: 3 participants
│ Silhouette: 0.682
│ Perfil característic:
│   Hores d'estudi setmanals: 22
│   Satisfacció amb els estudis: D'acord
│   Llenguatges de programació: Python
│ Membres:
│   - user1
│   - user3
│   - user5
└──────────────────────────────────────────────┘

┌─ CLUSTER 2 ─┐
│ Mida: 3 participants
│ Silhouette: 0.618
│ Perfil característic:
│   Hores d'estudi setmanals: 8
│   Satisfacció amb els estudis: En desacord
│   Llenguatges de programació: C++
│ Membres:
│   - user2
│   - user4
│   - user6
└──────────────────────────────────────────────┘

✓ Anàlisi completada i perfils assignats!
```

---

## 🎯 Ventajas de esta Arquitectura

### 1. **Mantenibilidad**
- Cambios en algoritmos de clustering no afectan a MainDriver
- Lógica de negocio centralizada en CtrlDomini
- Fácil añadir nuevos algoritmos (solo modificar CtrlAnalisi)

### 2. **Reutilización**
- Otros drivers pueden usar `ctrlDomini.analitzarEnquesta()`
- CtrlAnalisi es independiente y puede usarse en otras funcionalidades
- ResultatClustering es reutilizable para diferentes visualizaciones

### 3. **Testabilidad**
- Cada componente se puede testear independientemente
- Mock de CtrlAnalisi en tests de CtrlDomini
- Mock de CtrlDomini en tests de MainDriver

### 4. **Consistencia**
- Sigue el mismo patrón que otras operaciones del sistema
- MainDriver → CtrlDomini → CtrlSubsistema
- Persistencia automática sin código adicional

### 5. **Escalabilidad**
- Fácil añadir más métricas de evaluación (Davies-Bouldin, etc.)
- Posibilidad de clustering jerárquico sin cambiar MainDriver
- Preparado para guardar histórico de análisis

---

## 🔍 Detalles Técnicos

### Vectorización de Respuestas
```java
// Para cada usuario, construir vector en orden de preguntas
for (Pregunta p : preguntes) {
    Resposta r = respostesMap.get(p.getId());
    vector[i] = (r != null) ? r.getTextResposta() : "";
}
```

**Manejo de valores faltantes:** String vacío `""`

### Construcción de FeatureSpec
```java
FeatureSpecFactory.fromPreguntas(preguntes)
```

**Mapeo automático:**
- `NUMERICA` → `FeatureSpec.NUMERIC`
- `QUALITATIVA_ORDENADA` → `FeatureSpec.ORDINAL`
- `QUALITATIVA_NO_ORDENADA_SIMPLE` → `FeatureSpec.NOMINAL_SINGLE`
- `QUALITATIVA_NO_ORDENADA_MULTIPLE` → `FeatureSpec.NOMINAL_MULTI`
- `TEXT_LLIURE` → `FeatureSpec.FREE_TEXT`

### Cálculo de Silhouette
```java
ClusterEvaluator evaluator = new ClusterEvaluator();
double s_global = evaluator.silhouetteScore(clusters, specs);
double[] s_per_cluster = evaluator.silhouettePerCluster(clusters, specs);
```

**Interpretación:**
- `s ≥ 0.7`: Excelente
- `0.5 ≤ s < 0.7`: Bueno
- `0.25 ≤ s < 0.5`: Aceptable
- `s < 0.25`: Pobre

### Persistencia de Perfiles
```java
Usuari usuari = ctrlPersistencia.getUsuari(username);
usuari.assignarPerfil(idEnquesta, perfilCluster);
// No es necesario saveUsuari() - el objeto ya está en persistencia
```

Los perfiles se guardan en `HashMap<String, Perfil>` dentro de cada usuario, por lo que permanecen mientras el usuario esté en el sistema.

---

## 📝 Notas Importantes

### Requisitos para Clustering
- **Mínimo 2 usuarios** con respuestas
- **k ≥ 2** y **k ≤ número de usuarios**
- Las preguntas deben tener respuestas (vectores no vacíos)

### Limitaciones Actuales
- Los perfiles se pierden al reiniciar la aplicación (no hay persistencia en disco)
- Solo se guarda un perfil por enquesta por usuario (análisis múltiples sobrescriben)
- El nombre del cluster es heurístico (puede mejorarse)

### Mejoras Futuras Sugeridas
1. **Persistencia en disco**: Serializar HashMap de perfiles
2. **Histórico de análisis**: Guardar múltiples versiones (timestamp)
3. **Más algoritmos**: DBSCAN, Hierarchical Clustering
4. **Visualización**: Gráficos de clusters en 2D/3D (PCA/t-SNE)
5. **Comparación de usuarios**: "¿Qué tan similar eres a otros del cluster?"
6. **Exportación**: JSON/CSV con resultados del análisis

---

## 🧪 Verificación

Para verificar que todo funciona:

1. **Compilar:**
   ```bash
   cd ENTREGA/FONTS
   ./gradlew build
   ```

2. **Ejecutar MainDriver:**
   ```bash
   ./gradlew runDriver -PmainClass=edu.upc.prop.clusterxx.MainDriver
   ```

3. **Flujo de prueba:**
   - Registrar 4-6 usuarios
   - Crear enquesta con 3 preguntas (numèrica, ordenada, múltiple)
   - Cada usuario responde las 3 preguntas
   - Analizar enquesta (opción 11) con k=2, KMeans++
   - Ver perfil (opción 12)

4. **Resultado esperado:**
   - 2 clusters con usuarios agrupados por similitud
   - Silhouette > 0.4
   - Cada usuario tiene su perfil asignado
   - El perfil muestra cluster, centroide y calidad

---

## 📚 Referencias

- **KMeans/KMeans++**: `src/main/java/edu/upc/prop/clusterxx/domini/classes/KMeans*.java`
- **DistanceCalculator**: Cálculo de distancias heterogéneas
- **ClusterEvaluator**: Métricas de calidad (Silhouette)
- **Ejemplo completo**: `EJEMPLO_CLUSTERING.md`
- **JSON de ejemplo**: `exemple_enquesta_clustering.json`

---


