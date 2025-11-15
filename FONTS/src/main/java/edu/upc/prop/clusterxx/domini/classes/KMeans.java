package edu.upc.prop.clusterxx.domini.classes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Implementación del algoritmo K-Means para clustering de vectores heterogéneos.
 * 
 * <p>K-Means es un algoritmo de clustering particional que divide N puntos en K grupos
 * minimizando la suma de distancias de cada punto a su centroide más cercano.</p>
 * 
 * <p><b>Algoritmo:</b></p>
 * <ol>
 *   <li>Inicialización: Seleccionar K centroides iniciales (aleatorios o proporcionados)</li>
 *   <li>Asignación: Asignar cada punto al cluster con centroide más cercano</li>
 *   <li>Actualización: Recalcular centroides como agregación de puntos en cada cluster</li>
 *   <li>Repetir pasos 2-3 hasta convergencia o máximo de iteraciones</li>
 * </ol>
 * 
 * <p><b>Convergencia:</b> El algoritmo converge cuando los centroides no cambian entre iteraciones.</p>
 * 
 * <p><b>Manejo de clusters vacíos:</b> Si un cluster queda sin miembros tras la asignación,
 * se resembrará con un punto aleatorio del dataset para evitar divisiones por cero.</p>
 * 
 * <p><b>Métrica de distancia:</b> Usa distancia Euclidiana (L2) calculada por {@link DistanceCalculator}
 * con soporte para variables heterogéneas (NUMERIC, ORDINAL, NOMINAL_SINGLE, NOMINAL_MULTI, FREE_TEXT).</p>
 * 
 * <p><b>Nota:</b> K-Means NO garantiza encontrar el óptimo global. El resultado depende de la
 * inicialización. Para mejores resultados, considerar usar {@link KMeansPlusPlus} que implementa
 * una estrategia de inicialización más inteligente (K-Means++).</p>
 * 
 * @see Kluster
 * @see DistanceCalculator
 * @see KMeansPlusPlus
 */
public class KMeans {
    /** Calculadora de distancias entre vectores heterogéneos. */
    private final DistanceCalculator dc = new DistanceCalculator();
    
    /** Generador de números aleatorios para inicialización y manejo de clusters vacíos. */
    private final Random rnd;

    /**
     * Crea una instancia de K-Means con generador aleatorio por defecto.
     * 
     * <p>El generador aleatorio se usa para:
     * <ul>
     *   <li>Selección de centroides iniciales aleatorios en {@link #fit}</li>
     *   <li>Resembrado de clusters vacíos durante el entrenamiento</li>
     * </ul>
     */
    public KMeans() { this(new Random()); }
    
    /**
     * Crea una instancia de K-Means con generador aleatorio específico.
     * 
     * <p>Útil para reproducibilidad en tests: usar {@code new Random(seed)} con semilla fija.</p>
     * 
     * @param rnd Generador de números aleatorios (si es null, se crea uno nuevo)
     */
    public KMeans(Random rnd) { this.rnd = (rnd == null ? new Random() : rnd); }

    /**
     * Entrena K-Means con inicialización aleatoria de centroides.
     * 
     * <p><b>Proceso:</b></p>
     * <ol>
     *   <li>Selecciona K puntos distintos aleatorios como centroides iniciales</li>
     *   <li>Ejecuta el algoritmo K-Means estándar usando {@link #fitWithInitialCentroids}</li>
     * </ol>
     * 
     * <p><b>Limitaciones de inicialización aleatoria:</b></p>
     * <ul>
     *   <li>Puede converger a óptimos locales dependiendo de la inicialización</li>
     *   <li>No tiene en cuenta la distribución de los datos</li>
     *   <li>Para mejores resultados, considerar usar {@link KMeansPlusPlus}</li>
     * </ul>
     * 
     * @param data Dataset de vectores heterogéneos (cada elemento es String[])
     * @param k Número de clusters a formar (debe cumplir: 0 < k ≤ |data|)
     * @param maxIters Máximo de iteraciones permitidas (si ≤ 0, se usa 100 por defecto)
     * @param specs Especificaciones de tipo para cada dimensión de los vectores
     * @return Lista de K clusters con sus centroides y miembros asignados
     * @throws IllegalArgumentException si data es null/vacío, k inválido, o specs es null
     */
    public List<Kluster> fit(List<String[]> data, int k, int maxIters, DistanceCalculator.FeatureSpec[] specs) {
        if (data == null || data.isEmpty()) throw new IllegalArgumentException("data empty");
        if (k <= 0 || k > data.size()) throw new IllegalArgumentException("invalid k");
        if (maxIters <= 0) maxIters = 100;
        if (specs == null) throw new IllegalArgumentException("specs required");

        // Inicialización: escoger k puntos distintos
        List<String[]> init = pickDistinct(data, k);
        return fitWithInitialCentroids(data, init, maxIters, specs);
    }

    /**
     * Entrena K-Means con centroides iniciales proporcionados.
     * 
     * <p>Este es el método principal que implementa el algoritmo K-Means estándar:</p>
     * 
     * <p><b>Algoritmo iterativo:</b></p>
     * <pre>
     * Para cada iteración (hasta maxIters):
     *   1. Limpiar miembros de todos los clusters
     *   2. ASIGNACIÓN: Para cada punto x en data:
     *      - Calcular distancia a cada centroide
     *      - Asignar x al cluster con centroide más cercano
     *   3. MANEJO DE VACÍOS: Para cada cluster:
     *      - Si quedó sin miembros → resembrar con punto aleatorio
     *   4. ACTUALIZACIÓN: Para cada cluster:
     *      - Recalcular centroide según tipo de variables (ver {@link Kluster#recomputeCentroid})
     *   5. CONVERGENCIA: Si ningún centroide cambió → terminar
     * </pre>
     * 
     * <p><b>Criterios de parada:</b></p>
     * <ul>
     *   <li>Convergencia: Los centroides no cambian entre iteraciones</li>
     *   <li>Máximo de iteraciones alcanzado (maxIters)</li>
     * </ul>
     * 
     * <p><b>Manejo de clusters vacíos:</b> Si tras la asignación un cluster queda sin miembros
     * (puede ocurrir si su centroide está muy alejado), se resembrará con un punto aleatorio
     * del dataset para evitar problemas numéricos y mantener exactamente K clusters.</p>
     * 
     * <p><b>Complejidad:</b> O(I × N × K × D) donde:
     * <ul>
     *   <li>I = número de iteraciones (≤ maxIters)</li>
     *   <li>N = tamaño del dataset</li>
     *   <li>K = número de clusters</li>
     *   <li>D = dimensionalidad de los vectores</li>
     * </ul>
     * 
     * @param data Dataset de vectores heterogéneos (cada elemento es String[])
     * @param initialCentroids Centroides iniciales (K vectores String[])
     * @param maxIters Máximo de iteraciones permitidas
     * @param specs Especificaciones de tipo para cada dimensión de los vectores
     * @return Lista de K clusters con sus centroides finales y miembros asignados
     * @throws IllegalArgumentException si initialCentroids es null/vacío o specs es null
     */
    public List<Kluster> fitWithInitialCentroids(List<String[]> data, List<String[]> initialCentroids, int maxIters, DistanceCalculator.FeatureSpec[] specs) {
        if (initialCentroids == null || initialCentroids.size() == 0) throw new IllegalArgumentException("no initial centroids");
        if (specs == null) throw new IllegalArgumentException("specs required");
        int k = initialCentroids.size();
        List<Kluster> clusters = new ArrayList<>(k);
        for (String[] c : initialCentroids) clusters.add(new Kluster(c));

        for (int iter = 0; iter < maxIters; iter++) {
            // limpiar miembros
            for (Kluster cl : clusters) cl.clearMembers();
            // asignar
            for (String[] x : data) {
                int best = 0; double bestD = Double.POSITIVE_INFINITY;
                for (int j = 0; j < k; j++) {
                    double d = dc.distance(x, clusters.get(j).getCentroid(), specs);
                    if (d < bestD) { bestD = d; best = j; }
                }
                clusters.get(best).addMember(x);
            }
            // evitar clusters vacíos: resembrar con un punto aleatorio
            for (int j = 0; j < k; j++) {
                if (clusters.get(j).size() == 0) {
                    String[] seed = data.get(rnd.nextInt(data.size()));
                    clusters.set(j, new Kluster(seed));
                }
            }
            // recomputar centroides y comprobar cambios
            boolean changed = false;
            for (Kluster cl : clusters) {
                changed |= cl.recomputeCentroid(specs);
            }
            if (!changed) break;
        }
        return clusters;
    }

    /**
     * Selecciona K puntos distintos aleatorios del dataset.
     * 
     * <p>Usa un {@link HashSet} para garantizar que los índices sean únicos,
     * evitando seleccionar el mismo punto múltiples veces como centroide inicial.</p>
     * 
     * <p><b>Proceso:</b></p>
     * <ol>
     *   <li>Generar K índices aleatorios únicos en [0, data.size())</li>
     *   <li>Extraer los puntos correspondientes del dataset</li>
     * </ol>
     * 
     * <p><b>Complejidad esperada:</b> O(K) cuando K << N, puede ser O(K log K) en peor caso.</p>
     * 
     * @param data Dataset del cual seleccionar puntos
     * @param k Número de puntos distintos a seleccionar
     * @return Lista de K vectores String[] seleccionados aleatoriamente (sin repetición)
     */
    private List<String[]> pickDistinct(List<String[]> data, int k) {
        Set<Integer> idx = new HashSet<>();
        while (idx.size() < k) idx.add(rnd.nextInt(data.size()));
        List<String[]> res = new ArrayList<>(k);
        for (int i : idx) res.add(data.get(i));
        return res;
    }
}
