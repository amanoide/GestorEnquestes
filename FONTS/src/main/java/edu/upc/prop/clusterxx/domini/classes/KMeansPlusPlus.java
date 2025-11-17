package edu.upc.prop.clusterxx.domini.classes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Implementación del algoritmo K-Means++ para clustering de vectores heterogéneos.
 * 
 * K-Means++ es una mejora del algoritmo K-Means que usa una estrategia inteligente
 * de inicialización de centroides para mejorar la calidad del clustering y acelerar
 * la convergencia.
 * 
 * Ventajas sobre K-Means estándar:
 *   - Mejor calidad: Tiende a encontrar mejores óptimos locales
 *   - Convergencia más rápida: Reduce el número de iteraciones necesarias
 *   - Garantía teórica: O(log k)-aproximación al óptimo en expectativa
 *   - Mayor estabilidad: Menor varianza entre ejecuciones
 * 
 * Algoritmo de inicialización K-Means++:
 *   1. Seleccionar el primer centroide uniformemente al azar del dataset
 *   2. Para cada centroide adicional (hasta tener K):
 *      - Para cada punto x: calcular D(x)² = distancia² al centroide más cercano
 *      - Seleccionar siguiente centroide con probabilidad proporcional a D(x)²
 *   3. Ejecutar K-Means estándar con estos centroides iniciales
 * 
 * Intuición: La probabilidad proporcional a D(x)² favorece puntos alejados
 * de centroides existentes, distribuyendo los centroides iniciales de forma más uniforme
 * por el espacio y evitando agrupaciones locales.
 * 
 * Métrica de distancia: Usa distancia Euclidiana (L2) calculada por DistanceCalculator
 * con soporte para variables heterogéneas (NUMERIC, ORDINAL, NOMINAL_SINGLE, NOMINAL_MULTI, FREE_TEXT).
 * 
 * @see KMeans
 * @see Kluster
 * @see DistanceCalculator
 */
public class KMeansPlusPlus {
    /** Calculadora de distancias entre vectores heterogéneos. */
    private final DistanceCalculator dc = new DistanceCalculator();
    
    /** Generador de números aleatorios para selección probabilística de centroides. */
    private final Random rnd;

    /**
     * Crea una instancia de K-Means++ con generador aleatorio por defecto.
     * 
     * El generador aleatorio se usa para la selección probabilística de centroides
     * en el algoritmo K-Means++.
     */
    public KMeansPlusPlus() { this(new Random()); }
    
    /**
     * Crea una instancia de K-Means++ con generador aleatorio específico.
     * 
     * Útil para reproducibilidad en tests: usar new Random(seed) con semilla fija.
     * 
     * @param rnd Generador de números aleatorios (si es null, se crea uno nuevo)
     */
    public KMeansPlusPlus(Random rnd) { this.rnd = (rnd == null ? new Random() : rnd); }

    /**
     * Entrena K-Means++ ejecutando inicialización inteligente seguida de K-Means estándar.
     * 
     * Proceso completo:
     *   1. Inicialización K-Means++: initCentroids - selección probabilística
     *   2. Clustering K-Means: KMeans.fitWithInitialCentroids - iteraciones estándar
     * 
     * Complejidad de inicialización: O(N × K × D)
     * Donde N = tamaño del dataset, K = número de clusters, D = dimensionalidad de los vectores.
     * 
     * La complejidad total es dominada por K-Means (O(I × N × K × D)), pero K-Means++
     * típicamente requiere menos iteraciones (I) para converger.
     * 
     * @param data Dataset de vectores heterogéneos (cada elemento es String[])
     * @param k Número de clusters a formar (debe cumplir: 0 < k ≤ |data|)
     * @param maxIters Máximo de iteraciones para la fase K-Means
     * @param specs Especificaciones de tipo para cada dimensión de los vectores
     * @return Lista de K clusters con sus centroides finales y miembros asignados
     * @throws IllegalArgumentException si data es null/vacío, k inválido, o specs es null
     * @see #initCentroids
     */
    public List<Kluster> fit(List<String[]> data, int k, int maxIters, DistanceCalculator.FeatureSpec[] specs) {
        List<String[]> init = initCentroids(data, k, specs);
        return new KMeans(rnd).fitWithInitialCentroids(data, init, maxIters, specs);
    }

    /**
     * Inicializa K centroides usando el algoritmo K-Means++.
     * 
     * Este es el núcleo del algoritmo K-Means++ que implementa la selección
     * probabilística de centroides para mejorar la inicialización.
     * 
     * Algoritmo detallado:
     * 1. Seleccionar primer centroide c₁ uniformemente al azar
     * 2. Para i = 2 hasta K:
     *    a) Para cada punto x en data:
     *       - Calcular D(x) = distancia mínima de x a cualquier centroide ya seleccionado
     *       - Calcular D(x)² (cuadrado de la distancia)
     *    b) Seleccionar siguiente centroide cᵢ con probabilidad:
     *       P(x) = D(x)² / Σ D(y)²  (para todo y en data)
     *    c) Usar selección por ruleta (roulette wheel selection)
     * 3. Retornar lista de K centroides
     * 
     * Selección por ruleta: Técnica para muestreo probabilístico:
     *   - Generar número aleatorio r en [0, Σ D(x)²]
     *   - Recorrer puntos restando D(x)² hasta que r ≤ 0
     *   - El punto donde r cruza 0 es el seleccionado
     * 
     * Propiedad clave: Puntos alejados de centroides existentes tienen
     * mayor probabilidad de ser seleccionados, distribuyendo los centroides de forma
     * más uniforme por el espacio de datos.
     * 
     * Manejo de borde: Si el índice calculado supera el tamaño del dataset
     * (por redondeo numérico), se usa el último punto del dataset.
     * 
     * Complejidad: O(N × K × D)
     * Donde N = tamaño del dataset, K = número de clusters, D = dimensionalidad.
     * 
     * @param data Dataset de vectores heterogéneos
     * @param k Número de centroides a seleccionar (debe cumplir: 0 < k ≤ |data|)
     * @param specs Especificaciones de tipo para cada dimensión de los vectores
     * @return Lista de K vectores String[] seleccionados como centroides iniciales
     * @throws IllegalArgumentException si data es null/vacío, k inválido, o specs es null
     */
    public List<String[]> initCentroids(List<String[]> data, int k, DistanceCalculator.FeatureSpec[] specs) {
        if (data == null || data.isEmpty()) throw new IllegalArgumentException("data empty");
        if (k <= 0 || k > data.size()) throw new IllegalArgumentException("invalid k");
        if (specs == null) throw new IllegalArgumentException("specs required");

        List<String[]> centroids = new ArrayList<>();
        centroids.add(data.get(rnd.nextInt(data.size())));
        while (centroids.size() < k) {
            // calcular D(x)^2 para cada punto x (distancia al centroide más cercano)
            double[] d2 = new double[data.size()];
            double sum = 0.0;
            for (int i = 0; i < data.size(); i++) {
                double best = Double.POSITIVE_INFINITY;
                for (String[] c : centroids) {
                    best = Math.min(best, dc.distance(data.get(i), c, specs));
                }
                d2[i] = best * best;
                sum += d2[i];
            }
            // ruleta
            double r = rnd.nextDouble() * sum;
            int chosen = 0;
            for (; chosen < d2.length; chosen++) {
                r -= d2[chosen];
                if (r <= 0) break;
            }
            centroids.add(data.get(Math.min(chosen, data.size() - 1)));
        }
        return centroids;
    }
}
