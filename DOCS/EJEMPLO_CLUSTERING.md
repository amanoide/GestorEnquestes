# 📊 EJEMPLO COMPLETO: ANÁLISIS DE CLUSTERING PASO A PASO

## 🎯 ESCENARIO

**Encuesta**: "Satisfacción del Curso de Programación"
**Preguntas**:
1. P1: "¿Cuántas horas estudias por semana?" (NUMERICA: 0-50)
2. P2: "¿Cómo valoras el profesor?" (QUALITATIVA_ORDENADA: Malo, Regular, Bueno, Excelente)
3. P3: "¿Qué lenguajes programas?" (QUALITATIVA_NO_ORDENADA_MULTIPLE: Java, Python, C++)

**Participantes**: 6 usuarios

---

## 📝 PASO 1: RESPUESTAS DE LOS USUARIOS

```
Usuario: "alice"
├─ P1: "10"
├─ P2: "Excelente"
└─ P3: "Java,Python"

Usuario: "bob"
├─ P1: "8"
├─ P2: "Excelente"
└─ P3: "Java,Python,C++"

Usuario: "carol"
├─ P1: "35"
├─ P2: "Malo"
└─ P3: "C++"

Usuario: "david"
├─ P1: "40"
├─ P2: "Regular"
└─ P3: "C++"

Usuario: "eve"
├─ P1: "12"
├─ P2: "Bueno"
└─ P3: "Python"

Usuario: "frank"
├─ P1: "9"
├─ P2: "Excelente"
└─ P3: "Java"
```

---

## 🔄 PASO 2: VECTORIZACIÓN (MainDriver línea ~1047)

**Código en MainDriver**:
```java
List<String[]> dataVectors = new ArrayList<>();
for (String username : usernames) {
    String[] vector = new String[preguntes.size()];
    for (int i = 0; i < preguntes.size(); i++) {
        Pregunta p = preguntes.get(i);
        Resposta resposta = respostesMap.get(p.getId());
        vector[i] = resposta.getTextResposta();
    }
    dataVectors.add(vector);
}
```

**Resultado - dataVectors**:
```java
dataVectors = [
    ["10", "Excelente", "Java,Python"],      // alice
    ["8", "Excelente", "Java,Python,C++"],   // bob
    ["35", "Malo", "C++"],                   // carol
    ["40", "Regular", "C++"],                // david
    ["12", "Bueno", "Python"],               // eve
    ["9", "Excelente", "Java"]               // frank
]
```

---

## 🏭 PASO 3: CREAR FEATURESPECS (FeatureSpecFactory)

**Código**:
```java
DistanceCalculator.FeatureSpec[] specs = ctrlAnalisi.buildSpecsFromPreguntas(preguntes);
```

**Proceso interno en FeatureSpecFactory.fromPregunta()**:

```java
// Para P1: NUMERICA
Pregunta p1 = {id: "p1", text: "¿Cuántas horas...?", tipo: NUMERICA, min: 0, max: 50}
→ specs[0] = FeatureSpec.numeric(0.0, 50.0)

// Para P2: QUALITATIVA_ORDENADA
Pregunta p2 = {id: "p2", text: "¿Cómo valoras...?", tipo: QUALITATIVA_ORDENADA,
               opciones: [Malo(orden=0), Regular(orden=1), Bueno(orden=2), Excelente(orden=3)]}
→ List<String> orden = ["Malo", "Regular", "Bueno", "Excelente"]
→ Integer m = 4
→ specs[1] = FeatureSpec.ordinal(orden, 4)

// Para P3: QUALITATIVA_NO_ORDENADA_MULTIPLE
Pregunta p3 = {id: "p3", text: "¿Qué lenguajes...?", tipo: QUALITATIVA_NO_ORDENADA_MULTIPLE,
               opciones: [Java, Python, C++], maxSeleccions: 3}
→ Set<String> domain = {"Java", "Python", "C++"}
→ specs[2] = FeatureSpec.nominalMulti(domain, 3)
```

**Resultado - specs[]**:
```java
specs[0] = FeatureSpec{kind=NUMERIC, min=0.0, max=50.0}
specs[1] = FeatureSpec{kind=ORDINAL, order=["Malo","Regular","Bueno","Excelente"], m=4}
specs[2] = FeatureSpec{kind=NOMINAL_MULTI, domain={"Java","Python","C++"}, maxSel=3}
```

---

## 🎯 PASO 4: INICIALIZACIÓN KMEANS++ (k=2 clusters)

**Usuario selecciona**: k=2, algoritmo=KMeans++

**KMeansPlusPlus.initCentroids()**:

```java
// Iteración 1: Seleccionar primer centroide aleatorio
random.nextInt(6) → 1
centroids[0] = dataVectors[1] = ["8", "Excelente", "Java,Python,C++"]  // bob

// Iteración 2: Calcular D(x)² para cada punto
Para cada usuario, calcular distancia al centroide más cercano:

alice  ["10", "Excelente", "Java,Python"]
  vs bob ["8", "Excelente", "Java,Python,C++"]
  → DistanceCalculator.distance(alice, bob, specs)
  
  Dimensión 0 (NUMERIC): |10-8| / 50 = 0.04
  Dimensión 1 (ORDINAL): |3-3| / 3 = 0.0
  Dimensión 2 (NOMINAL_MULTI): 
    alice: {Java, Python}
    bob: {Java, Python, C++}
    intersección: {Java, Python} = 2
    unión: {Java, Python, C++} = 3
    Jaccard = 2/3 = 0.67
    Distancia = 1 - 0.67 = 0.33
  
  Distancia total = sqrt(0.04² + 0² + 0.33²) / 3 = 0.11
  D²(alice) = 0.0121

carol  ["35", "Malo", "C++"]
  vs bob ["8", "Excelente", "Java,Python,C++"]
  
  Dimensión 0 (NUMERIC): |35-8| / 50 = 0.54
  Dimensión 1 (ORDINAL): |0-3| / 3 = 1.0
  Dimensión 2 (NOMINAL_MULTI):
    carol: {C++}
    bob: {Java, Python, C++}
    intersección: {C++} = 1
    unión: 3
    Jaccard = 0.33
    Distancia = 0.67
  
  Distancia total = sqrt(0.54² + 1² + 0.67²) / 3 = 0.43
  D²(carol) = 0.1849

david  ["40", "Regular", "C++"]
  D²(david) = 0.1936 (similar a carol)

eve    ["12", "Bueno", "Python"]
  D²(eve) = 0.0225

frank  ["9", "Excelente", "Java"]
  D²(frank) = 0.0081

// Suma total de D²
sum = 0.0121 + 0.1849 + 0.1936 + 0.0225 + 0.0081 = 0.4212

// Ruleta (selección proporcional)
random.nextDouble() * 0.4212 → 0.25
0.0121 → no
0.0121 + 0.1849 = 0.197 → no
0.197 + 0.1936 = 0.3906 → SÍ, se selecciona david

centroids[1] = dataVectors[3] = ["40", "Regular", "C++"]  // david
```

**Centroides iniciales**:
```java
Cluster 0: centroid = ["8", "Excelente", "Java,Python,C++"]
Cluster 1: centroid = ["40", "Regular", "C++"]
```

---

## 🔄 PASO 5: ITERACIÓN KMEANS (KMeans.fitWithInitialCentroids)

### **Iteración 1 - Asignación**

**Para cada usuario, encontrar cluster más cercano**:

```java
alice ["10", "Excelente", "Java,Python"]
  → distancia a Cluster 0 = 0.11
  → distancia a Cluster 1 = 0.35
  → Asignado a Cluster 0

bob ["8", "Excelente", "Java,Python,C++"]
  → distancia a Cluster 0 = 0.0
  → distancia a Cluster 1 = 0.43
  → Asignado a Cluster 0

carol ["35", "Malo", "C++"]
  → distancia a Cluster 0 = 0.43
  → distancia a Cluster 1 = 0.11
  → Asignado a Cluster 1

david ["40", "Regular", "C++"]
  → distancia a Cluster 0 = 0.45
  → distancia a Cluster 1 = 0.0
  → Asignado a Cluster 1

eve ["12", "Bueno", "Python"]
  → distancia a Cluster 0 = 0.15
  → distancia a Cluster 1 = 0.37
  → Asignado a Cluster 0

frank ["9", "Excelente", "Java"]
  → distancia a Cluster 0 = 0.09
  → distancia a Cluster 1 = 0.42
  → Asignado a Cluster 0
```

**Resultado**:
```
Cluster 0: {alice, bob, eve, frank}
Cluster 1: {carol, david}
```

### **Iteración 1 - Recalcular Centroides (Kluster.recomputeCentroid)**

**Cluster 0** (4 miembros):
```java
members = [
    ["10", "Excelente", "Java,Python"],
    ["8", "Excelente", "Java,Python,C++"],
    ["12", "Bueno", "Python"],
    ["9", "Excelente", "Java"]
]

// Dimensión 0 (NUMERIC): Media aritmética
valores = [10, 8, 12, 9]
media = (10 + 8 + 12 + 9) / 4 = 9.75 ≈ 10
newCentroid[0] = "10"

// Dimensión 1 (ORDINAL): Moda (valor más frecuente)
valores = ["Excelente", "Excelente", "Bueno", "Excelente"]
frecuencias = {Excelente: 3, Bueno: 1}
moda = "Excelente"
newCentroid[1] = "Excelente"

// Dimensión 2 (NOMINAL_MULTI): Moda
valores = ["Java,Python", "Java,Python,C++", "Python", "Java"]
frecuencias = {
    "Java,Python": 1,
    "Java,Python,C++": 1,
    "Python": 1,
    "Java": 1
}
// Empate → mantener actual
newCentroid[2] = "Java,Python,C++" (centroid actual)

newCentroid = ["10", "Excelente", "Java,Python,C++"]
```

**Cluster 1** (2 miembros):
```java
members = [
    ["35", "Malo", "C++"],
    ["40", "Regular", "C++"]
]

// Dimensión 0 (NUMERIC): Media
media = (35 + 40) / 2 = 37.5 ≈ 38
newCentroid[0] = "38"

// Dimensión 1 (ORDINAL): Moda
valores = ["Malo", "Regular"]
frecuencias = {Malo: 1, Regular: 1}
// Empate → mantener actual
newCentroid[1] = "Regular"

// Dimensión 2 (NOMINAL_MULTI): Moda
valores = ["C++", "C++"]
frecuencias = {C++: 2}
newCentroid[2] = "C++"

newCentroid = ["38", "Regular", "C++"]
```

**Nuevos centroides**:
```
Cluster 0: ["10", "Excelente", "Java,Python,C++"]
Cluster 1: ["38", "Regular", "C++"]
```

### **Iteración 2 - Reasignación**

```java
// Los centroides cambiaron ligeramente
// Cluster 0: ["8"→"10", "Excelente", "Java,Python,C++"]
// Cluster 1: ["40"→"38", "Regular", "C++"]

// Reasignar cada usuario...
// (En este caso, probablemente las asignaciones no cambien mucho)

alice → Cluster 0
bob → Cluster 0
carol → Cluster 1
david → Cluster 1
eve → Cluster 0
frank → Cluster 0

// No hay cambios → CONVERGENCIA
```

**Algoritmo termina**.

---

## 📊 PASO 6: EVALUACIÓN SILHOUETTE (ClusterEvaluator)

**Para cada usuario, calcular s(i) = (b - a) / max(a, b)**

### **Ejemplo: alice en Cluster 0**

```java
// a(i): distancia media a otros del MISMO cluster
otros_cluster0 = [bob, eve, frank]
distancias = [
    distance(alice, bob) = 0.11,
    distance(alice, eve) = 0.08,
    distance(alice, frank) = 0.05
]
a = (0.11 + 0.08 + 0.05) / 3 = 0.08

// b(i): distancia media mínima a OTROS clusters
// Solo hay Cluster 1
otros_cluster1 = [carol, david]
distancias = [
    distance(alice, carol) = 0.35,
    distance(alice, david) = 0.38
]
b_cluster1 = (0.35 + 0.38) / 2 = 0.365
b = 0.365  // mínimo (solo hay un otro cluster)

// Silhouette
s(alice) = (0.365 - 0.08) / max(0.365, 0.08)
         = 0.285 / 0.365
         = 0.78
```

### **Cálculo para todos**:

```
s(alice) = 0.78
s(bob) = 0.82
s(eve) = 0.75
s(frank) = 0.80

s(carol) = 0.71
s(david) = 0.68

Silhouette promedio Cluster 0 = (0.78 + 0.82 + 0.75 + 0.80) / 4 = 0.79
Silhouette promedio Cluster 1 = (0.71 + 0.68) / 2 = 0.70

Silhouette GLOBAL = (0.78 + 0.82 + 0.75 + 0.80 + 0.71 + 0.68) / 6 = 0.76
```

---

## 🎨 PASO 7: PRESENTACIÓN DE RESULTADOS (MainDriver)

```
═══════════════════════════════════════════
      RESULTATS DEL CLUSTERING
═══════════════════════════════════════════

📊 Algoritme: KMeans++
📊 Nombre de clusters: 2
📊 Participants analitzats: 6
📊 Coeficient Silhouette global: 0.759
   Qualitat: Excel·lent ✓✓✓ (clusters ben separats i compactes)

┌─ CLUSTER 1 ─┐
│ Mida: 4 participants
│ Silhouette: 0.788
│ Perfil característic:
│   ¿Cuántas horas estudias?: 10
│   ¿Cómo valoras el profesor?: Excelente
│   ¿Qué lenguajes programas?: Java,Python,C++
│ Membres:
│   - alice
│   - bob
│   - eve
│   - frank
└──────────────────────────────────────────┘

┌─ CLUSTER 2 ─┐
│ Mida: 2 participants
│ Silhouette: 0.695
│ Perfil característic:
│   ¿Cuántas horas estudias?: 38
│   ¿Cómo valoras el profesor?: Regular
│   ¿Qué lenguajes programas?: C++
│ Membres:
│   - carol
│   - david
└──────────────────────────────────────────┘

💡 Interpretació:
   - Cluster 1: Estudiantes dedicados, satisfechos, conocen varios lenguajes
   - Cluster 2: Estudiantes que dedican mucho tiempo pero no están satisfechos
```

---

## 📌 RESUMEN DEL FLUJO DE DATOS

```
1. RESPUESTAS (HashMap) 
   → {"alice" → [Resposta(P1,"10"), Resposta(P2,"Excelente"), ...]}

2. VECTORIZACIÓN (List<String[]>)
   → [["10","Excelente","Java,Python"], ["8","Excelente","Java,Python,C++"], ...]

3. FEATURESPECS (FeatureSpec[])
   → [NUMERIC(0-50), ORDINAL([...]), NOMINAL_MULTI([...])]

4. KMEANS++ INIT (List<String[]>)
   → Selecciona centroides inteligentemente

5. CLUSTERING (List<Kluster>)
   → Itera: asignar → recalcular → repetir

6. EVALUACIÓN (double)
   → Calcula Silhouette para validar calidad

7. PRESENTACIÓN (String)
   → Muestra resultados en formato legible
```

---

## 🎯 CONCLUSIÓN

**Los datos viajan así**:
```
Base de datos (Resposta)
    ↓
Vectores String[] (formato uniforme)
    ↓
DistanceCalculator (con FeatureSpec[] como guía)
    ↓
KMeans/KMeans++ (agrupa vectores similares)
    ↓
Kluster[] (grupos con centroides)
    ↓
ClusterEvaluator (valida calidad)
    ↓
Usuario (visualiza resultados)
```

**FeatureSpec** es el "manual de instrucciones" que viaja junto con los datos y le dice a cada componente "cómo interpretar cada columna".
