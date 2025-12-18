# Resum del Document

Aquest document descriu de manera exhaustiva els algoritmes de clustering implementats en el nostre projecte. Per a cadascun dels algoritmes, hem analitzat:
• Estructura triada: Les decisions de disseny preses per implementar l’algoritme.
• Justificació: Per què hem escollit aquesta estructura i com funciona.
• Alternatives descartades: Altres opcions que vam considerar i per què les hem rebutjat.
• Complexitat: Anàlisi de la complexitat temporal i espacial.

Els algoritmes implementats són K-Means, K-Means++ i K-Medoids (PAM), a més de les classes auxiliars DistanceCalculator, ClusterEvaluator i Kluster que donen suport al sistema de clustering.

El document està pensat per explicar les nostres decisions d’implementació de manera que qualsevol persona del grup pugui entendre per què hem fet les coses d’una manera determinada i quines alternatives hem considerat.

---

## 1. Algoritme K-Means

### Estructura triada
Hem implementat l'algoritme de **K-Means estàndard (algoritme de Lloyd)**.
- **Representació de dades:** `List<String[]>` per manejar vectors heterogenis (dades crues).
- **Estructura de clusters:** `ArrayList<Kluster>`, on cada objecte `Kluster` manté el seu centroide i una llista dels punts assignats.
- **Inicialització:** Selecció aleatòria de $K$ punts diferents del dataset (`pickDistinct`).
- **Assignació:** Iterativa basada en la distància Euclidiana més propera.

### Justificació
K-Means és l'algoritme de clustering més fonamental i eficient per a dades on es pot definir un concepte de "mitjana".
- Hem optat per re-calcular els centroides en cada iteració delegant la lògica a la classe `Kluster`, que sap com calcular la "mitjana" per a diferents tipus de dades (mitjana aritmètica per a numèrics, moda per a categòrics).
- S'inclou un mecanisme de seguretat: si un cluster queda buit durant l'assignació (pot passar amb una mala inicialització), es re-sembra amb un punt aleatori per evitar divisions per zero i garantir que sempre retornem $K$ clusters.

### Alternatives descartades
- **Mini-Batch K-Means:** Considerat per a datasets molt grans, però descartat perquè el nostre volum de dades (enquestes) s'ajusta bé a la memòria i K-Means estàndard ofereix més precisió (menys soroll en la convergència).
- **Bisecting K-Means:** Descartat per simplicitat, ja que K-Means++ (veure següent punt) ja resol gran part dels problemes d'inicialització que Bisecting K-Means intenta mitigar.

### Complexitat
- **Temporal:** $O(I \cdot N \cdot K \cdot D)$
  - $I$: Nombre d'iteracions (fixat a màx 100).
  - $N$: Nombre d'elements al dataset.
  - $K$: Nombre de clusters.
  - $D$: Dimensionalitat (nombre de preguntes).
  - És un algoritme lineal respecte a $N$, la qual cosa el fa molt ràpid.
- **Espacial:** $O(N \cdot D + K \cdot D)$ per emmagatzemar les dades i els centroides.

---

## 2. Algoritme K-Means++

### Estructura triada
Aquesta classe **estén** la lògica de K-Means, substituint únicament la fase d'inicialització.
- **Estratègia d'inicialització:** Probabilística proporcional al quadrat de la distància ($D(x)^2$).
- **Implementació:** Utilitza el mètode de la "ruleta" (Roulette Wheel Selection) per seleccionar nous centroides basant-se en les distàncies acumulades als centroides ja existents.

### Justificació
K-Means pateix molt per la seva dependència de la inicialització inicial; una mala tria pot portar a òptims locals pobres.
- K-Means++ garanteix matemàticament (en expectativa) una solució $O(\log k)$-competitiva respecte a l'òptim.
- A la pràctica, fa que l'algoritme convergeixi dràsticament més ràpid (menys iteracions) i amb millors resultats (menor error quadràtic), justificant el cost extra de la inicialització.

### Alternatives descartades
- **Inicialització determinista (ex: punts més allunyats):** Descartada perquè és molt sensible a *outliers* (punts aïllats), que serien seleccionats sempre com a centroides inicials, espatllant el clustering. K-Means++ és un compromís robust entre aleatorietat i distància.

### Complexitat
- **Inicialització:** $O(N \cdot K \cdot D)$. Requereix fer passades sobre les dades per calcular distàncies per a cada nou centroide.
- **Global:** La complexitat asimptòtica és la mateixa que K-Means, però normalment $I$ (iteracions) es redueix significativament.

---

## 3. Algoritme K-Medoids (PAM - Partitioning Around Medoids)

### Estructura triada
Hem implementat l'algoritme **PAM (Partitioning Around Medoids)**.
- **Centres:** Utilitza índexs (`int[] medoidIdx`) que apunten a elements reals del dataset, no mitjanes calculades.
- **Distància:** Utilitza la distància **Manhattan (L1)** en lloc de l'Euclidiana per ser més robusta.
- **Criteri d'actualització:** Cerca exhaustiva dins del cluster per trobar el punt que minimitza la suma de distàncies a la resta de membres (el medoide òptim).

### Justificació
K-Medoids és crucial quan:
1. Volem que el representant del cluster sigui un element **real** existent (ex: "l'usuari prototip", no "un usuari mitjana inventat").
2. Hi ha soroll o *outliers*. K-Means és molt sensible als *outliers* (la mitjana es desplaça molt), mentre que K-Medoids és robust.
3. Treballem amb dades mixtes on calcular una "mitjana" és ambigu, però calcular distàncies és fàcil.

### Alternatives descartades
- **CLARA (Clustering Large Applications):** Variant de PAM que treballa amb mostres aleatòries per millorar l'eficiència. Descartada perquè el nostre dataset no és massiu (Big Data) i volíem la precisió exacta de PAM, no una aproximació.

### Complexitat
- **Temporal:** $O(I \cdot K \cdot (N-K)^2 \cdot D) \approx O(I \cdot N^2 \cdot D)$
  - El pas d'actualització és costós perquè, per a cada cluster, hem de provar cada membre com a possible nou medoide i calcular la distància a tots els altres membres.
  - Això el fa quadràtic i notablement més lent que K-Means per a $N$ gran.
- **Espacial:** $O(N^2)$ si pre-calculéssim la matriu de distàncies (no ho fem per estalviar memòria RAM, ho calculem al vol, així que es manté en $O(N \cdot D)$).

---

## 4. Classes Auxiliars

### DistanceCalculator
- **Funció:** Abstrau el càlcul de distàncies (Euclidiana i Manhattan).
- **Justificació:** Permet suportar atributs **heterogenis** de manera transparent. Normalitza automàticament els valors numèrics (max-min) i defineix distàncies per a atributs categòrics (0 si són iguals, 1 si són diferents).

### Kluster
- **Funció:** Contenidor de lògica d'estat del cluster.
- **Justificació:** Encapsula la complexa lògica de "recalcular el centroide" (`recomputeCentroid`). Això neteja el codi dels algoritmes principals, que només han de cridar a `cluster.recomputeCentroid()`.
- **Implementació:** Manté sumes acumulades per a mitjanes i comptadors de freqüència per a modes (atributs categòrics), fent el recàlcul eficient.
