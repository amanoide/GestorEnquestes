package edu.upc.prop.clusterxx.domini.classes;

import java.util.List;

/**
 * Evaluador de calidad de clustering mediante coeficiente de Silhouette.
 * 
 * <p>Proporciona métricas para evaluar la calidad de los resultados del clustering,
 * permitiendo comparar diferentes algoritmos o distintos valores de K.</p>
 * 
 * <p><b>Coeficiente de Silhouette:</b></p>
 * <p>Métrica que mide qué tan similar es un punto a su propio cluster (cohesión)
 * comparado con otros clusters (separación). Combina dos conceptos:</p>
 * <ul>
 *   <li><b>a(i):</b> Distancia media intra-cluster (cohesión) - queremos que sea pequeña</li>
 *   <li><b>b(i):</b> Distancia media al cluster más cercano (separación) - queremos que sea grande</li>
 * </ul>
 * 
 * <p><b>Fórmula por punto:</b> s(i) = (b(i) - a(i)) / max(a(i), b(i))</p>
 * 
 * <p><b>Interpretación de valores:</b></p>
 * <ul>
 *   <li><b>s(i) ≈ 1:</b> Punto muy bien asignado (lejos de otros clusters, cerca del propio)</li>
 *   <li><b>s(i) ≈ 0:</b> Punto en el borde entre clusters (ambiguo)</li>
 *   <li><b>s(i) < 0:</b> Punto posiblemente mal asignado (más cerca de otro cluster)</li>
 * </ul>
 * 
 * <p><b>Rango:</b> [-1, 1]</p>
 * 
 * <p><b>Ventajas:</b></p>
 * <ul>
 *   <li>No requiere conocer las etiquetas verdaderas (métrica no supervisada)</li>
 *   <li>Intuitivo: mide cohesión y separación simultáneamente</li>
 *   <li>Útil para seleccionar el número óptimo de clusters K</li>
 *   <li>Permite identificar clusters mal definidos</li>
 * </ul>
 * 
 * <p><b>Limitaciones:</b></p>
 * <ul>
 *   <li>Complejidad: O(N² × D) - costoso para datasets grandes</li>
 *   <li>Favorece clusters convexos y compactos</li>
 *   <li>Sensible a la métrica de distancia usada</li>
 * </ul>
 * 
 * <p><b>Uso típico:</b></p>
 * <pre>
 * ClusterEvaluator evaluator = new ClusterEvaluator();
 * double score = evaluator.silhouetteScore(clusters, specs);
 * // score > 0.5 → clustering bueno
 * // score > 0.7 → clustering muy bueno
 * </pre>
 * 
 * @see KMeans
 * @see KMeansPlusPlus
 * @see KMedoids
 * @see DistanceCalculator
 */
public class ClusterEvaluator {
    /** Calculadora de distancias entre vectores heterogéneos. */
    /** Calculadora de distancias entre vectores heterogéneos. */
    private final DistanceCalculator dc = new DistanceCalculator();

    /**
     * Calcula el coeficiente de Silhouette promedio para todo el clustering.
     * 
     * <p>Esta es la métrica principal para evaluar la calidad global del clustering.
     * Promedia el coeficiente de Silhouette de todos los puntos en todos los clusters.</p>
     * 
     * <p><b>Proceso de cálculo:</b></p>
     * <ol>
     *   <li>Para cada punto en cada cluster:
     *     <ul>
     *       <li>Calcular a(i): distancia media a puntos del mismo cluster</li>
     *       <li>Calcular b(i): distancia media mínima a puntos de otros clusters</li>
     *       <li>Calcular s(i) = (b(i) - a(i)) / max(a(i), b(i))</li>
     *     </ul>
     *   </li>
     *   <li>Promediar s(i) para todos los puntos</li>
     * </ol>
     * 
     * <p><b>Casos especiales:</b></p>
     * <ul>
     *   <li>K = 1 (un solo cluster): Retorna 0.0 (Silhouette no está definido)</li>
     *   <li>Clusters vacíos: Se ignoran en el cálculo</li>
     *   <li>Sin puntos: Retorna 0.0</li>
     * </ul>
     * 
     * <p><b>Interpretación del resultado:</b></p>
     * <ul>
     *   <li><b>> 0.7:</b> Clustering muy bueno - estructura fuerte</li>
     *   <li><b>0.5 - 0.7:</b> Clustering razonable - estructura moderada</li>
     *   <li><b>0.25 - 0.5:</b> Clustering débil - estructura poco clara</li>
     *   <li><b>< 0.25:</b> No hay estructura clara, considerar otro K o algoritmo</li>
     * </ul>
     * 
     * <p><b>Complejidad:</b> O(N² × K × D) donde:
     * <ul>
     *   <li>N = número total de puntos</li>
     *   <li>K = número de clusters</li>
     *   <li>D = dimensionalidad de los vectores</li>
     * </ul>
     * 
     * @param clusters Lista de clusters resultantes del algoritmo de clustering
     * @param specs Especificaciones de tipo para cada dimensión de los vectores
     * @return Coeficiente de Silhouette promedio en el rango [-1, 1]
     * @throws IllegalArgumentException si clusters o specs son null/vacíos
     */
    public double silhouetteScore(List<Kluster> clusters, DistanceCalculator.FeatureSpec[] specs) {
        if (clusters == null || clusters.isEmpty()) {
            throw new IllegalArgumentException("Clusters cannot be null or empty");
        }
        if (specs == null) {
            throw new IllegalArgumentException("FeatureSpec[] cannot be null");
        }
        
        // Caso especial: solo un cluster (Silhouette no está definido)
        if (clusters.size() == 1) {
            return 0.0;
        }
        
        double totalSilhouette = 0.0;
        int totalPoints = 0;
        
        // Calcular Silhouette para cada punto en cada cluster
        for (int clusterIndex = 0; clusterIndex < clusters.size(); clusterIndex++) {
            Kluster cluster = clusters.get(clusterIndex);
            List<String[]> members = cluster.getMembers();
            
            for (String[] point : members) {
                double s = silhouettePoint(point, clusterIndex, clusters, specs);
                totalSilhouette += s;
                totalPoints++;
            }
        }
        
        // Si no hay puntos, devolver 0
        if (totalPoints == 0) return 0.0;
        
        return totalSilhouette / totalPoints;
    }

    /**
     * Calcula el coeficiente de Silhouette para un punto individual.
     * 
     * <p>Este método implementa la fórmula estándar de Silhouette para un punto:</p>
     * 
     * <p><b>Fórmula:</b> s(i) = (b(i) - a(i)) / max(a(i), b(i))</p>
     * 
     * <p><b>Donde:</b></p>
     * <ul>
     *   <li><b>a(i):</b> Distancia media del punto a todos los demás puntos en su mismo cluster
     *     <ul><li>Mide cohesión (qué tan bien encaja en su cluster)</li></ul>
     *   </li>
     *   <li><b>b(i):</b> Distancia media mínima del punto a puntos de otros clusters
     *     <ul><li>Mide separación (qué tan alejado está de otros clusters)</li></ul>
     *   </li>
     * </ul>
     * 
     * <p><b>Casos especiales:</b></p>
     * <ul>
     *   <li>Cluster con un solo punto: a(i) no está definido → retorna 0.0</li>
     *   <li>a(i) = b(i) = 0: División por cero → retorna 0.0</li>
     * </ul>
     * 
     * <p><b>Interpretación:</b></p>
     * <ul>
     *   <li>s(i) > 0: b(i) > a(i) → punto más cerca de su cluster que de otros (bien asignado)</li>
     *   <li>s(i) = 0: b(i) = a(i) → punto equidistante (en el borde)</li>
     *   <li>s(i) < 0: b(i) < a(i) → punto más cerca de otro cluster (mal asignado)</li>
     * </ul>
     * 
     * @param point El punto a evaluar
     * @param clusterIndex Índice del cluster al que pertenece el punto
     * @param clusters Lista de todos los clusters
     * @param specs Especificaciones de tipo para calcular distancias
     * @return Coeficiente de Silhouette del punto en el rango [-1, 1]
     */
    private double silhouettePoint(String[] point, int clusterIndex, 
                                    List<Kluster> clusters, DistanceCalculator.FeatureSpec[] specs) {
        // a(i): distancia media intra-cluster (a puntos del mismo cluster)
        double a = intraClusterDistance(point, clusterIndex, clusters, specs);
        
        // b(i): distancia media mínima a otros clusters
        double b = minInterClusterDistance(point, clusterIndex, clusters, specs);
        
        // Caso especial: cluster con un solo punto
        if (Double.isNaN(a)) {
            return 0.0;
        }
        
        // s(i) = (b - a) / max(a, b)
        double maxDist = Math.max(a, b);
        if (maxDist == 0.0) return 0.0;
        
        return (b - a) / maxDist;
    }

    /**
     * Calcula la distancia media intra-cluster (cohesión).
     * 
     * <p>Calcula a(i): la distancia media del punto a todos los demás puntos
     * de su mismo cluster. Esta métrica mide qué tan cohesivo es el cluster
     * (qué tan cerca están sus miembros entre sí).</p>
     * 
     * <p><b>Fórmula:</b> a(i) = (1 / |Cᵢ| - 1) × Σ d(i, j) para todo j ∈ Cᵢ, j ≠ i</p>
     * 
     * <p>Donde Cᵢ es el cluster al que pertenece el punto i.</p>
     * 
     * <p><b>Interpretación:</b></p>
     * <ul>
     *   <li><b>a(i) pequeño:</b> Punto muy cerca de sus vecinos → cluster cohesivo</li>
     *   <li><b>a(i) grande:</b> Punto alejado de sus vecinos → cluster disperso</li>
     * </ul>
     * 
     * <p><b>Caso especial:</b> Si el cluster tiene solo un punto (|Cᵢ| = 1),
     * a(i) no está definido y se retorna {@code Double.NaN}. El método
     * {@link #silhouettePoint} maneja este caso retornando 0.0.</p>
     * 
     * @param point El punto a evaluar
     * @param clusterIndex Índice del cluster al que pertenece
     * @param clusters Lista de todos los clusters
     * @param specs Especificaciones de tipo para calcular distancias
     * @return Distancia media intra-cluster a(i), o {@code Double.NaN} si cluster con 1 solo punto
     */
    private double intraClusterDistance(String[] point, int clusterIndex, 
                                         List<Kluster> clusters, DistanceCalculator.FeatureSpec[] specs) {
        Kluster ownCluster = clusters.get(clusterIndex);
        List<String[]> members = ownCluster.getMembers();
        
        // Si el cluster tiene solo un punto, a(i) no está definido
        if (members.size() <= 1) {
            return Double.NaN;
        }
        
        double sumDistances = 0.0;
        int count = 0;
        
        for (String[] other : members) {
            // No calcular distancia del punto consigo mismo
            if (java.util.Arrays.equals(other, point)) continue;
            
            sumDistances += dc.distance(point, other, specs);
            count++;
        }
        
        return count > 0 ? sumDistances / count : 0.0;
    }

    /**
     * Calcula la distancia media mínima inter-cluster (separación).
     * 
     * <p>Calcula b(i): la distancia media mínima del punto a puntos de otros clusters.
     * Esta métrica mide qué tan separado está el punto de los clusters vecinos.</p>
     * 
     * <p><b>Proceso:</b></p>
     * <ol>
     *   <li>Para cada cluster Cⱼ diferente al propio (Cᵢ):
     *     <ul>
     *       <li>Calcular distancia media del punto a todos los puntos de Cⱼ</li>
     *     </ul>
     *   </li>
     *   <li>b(i) = mínimo de todas esas distancias medias</li>
     * </ol>
     * 
     * <p><b>Fórmula:</b> b(i) = min { (1/|Cⱼ|) × Σ d(i, j) para todo j ∈ Cⱼ } para todo Cⱼ ≠ Cᵢ</p>
     * 
     * <p><b>Interpretación:</b></p>
     * <ul>
     *   <li><b>b(i) grande:</b> Punto muy alejado de otros clusters → buena separación</li>
     *   <li><b>b(i) pequeño:</b> Punto cerca de otro cluster → posible frontera o mala asignación</li>
     *   <li><b>b(i) < a(i):</b> Punto más cerca de otro cluster que del propio → mal asignado</li>
     * </ul>
     * 
     * <p><b>Nota:</b> Se usa la distancia al cluster <i>más cercano</i> (no el promedio a todos)
     * porque queremos saber si el punto está bien asignado respecto a su mejor alternativa.</p>
     * 
     * @param point El punto a evaluar
     * @param clusterIndex Índice del cluster al que pertenece
     * @param clusters Lista de todos los clusters
     * @param specs Especificaciones de tipo para calcular distancias
     * @return Distancia media mínima a otros clusters b(i), o {@code Double.POSITIVE_INFINITY} si no hay otros clusters
     */
    private double minInterClusterDistance(String[] point, int clusterIndex, 
                                            List<Kluster> clusters, DistanceCalculator.FeatureSpec[] specs) {
        double minAvgDistance = Double.POSITIVE_INFINITY;
        
        for (int j = 0; j < clusters.size(); j++) {
            // Saltar el cluster propio
            if (j == clusterIndex) continue;
            
            Kluster otherCluster = clusters.get(j);
            List<String[]> otherMembers = otherCluster.getMembers();
            
            // Calcular distancia media a este cluster
            double sumDistances = 0.0;
            int count = 0;
            
            for (String[] other : otherMembers) {
                sumDistances += dc.distance(point, other, specs);
                count++;
            }
            
            double avgDistance = count > 0 ? sumDistances / count : Double.POSITIVE_INFINITY;
            minAvgDistance = Math.min(minAvgDistance, avgDistance);
        }
        
        return minAvgDistance;
    }

    /**
     * Calcula el coeficiente de Silhouette promedio por cluster.
     * 
     * <p>Útil para identificar qué clusters están bien definidos y cuáles tienen
     * problemas. Un cluster con Silhouette bajo puede indicar:</p>
     * <ul>
     *   <li>Cluster demasiado disperso (baja cohesión)</li>
     *   <li>Cluster superpuesto con otros (baja separación)</li>
     *   <li>Puntos outliers que deberían estar en otro cluster</li>
     *   <li>K demasiado grande (sobresegmentación)</li>
     * </ul>
     * 
     * <p><b>Proceso:</b></p>
     * <ol>
     *   <li>Para cada cluster Cⱼ:
     *     <ul>
     *       <li>Calcular s(i) para cada punto i ∈ Cⱼ</li>
     *       <li>Promediar todos los s(i) del cluster</li>
     *     </ul>
     *   </li>
     *   <li>Retornar array con el promedio de cada cluster</li>
     * </ol>
     * 
     * <p><b>Interpretación por cluster:</b></p>
     * <ul>
     *   <li><b>score[j] > 0.5:</b> Cluster j bien definido</li>
     *   <li><b>score[j] ≈ 0:</b> Cluster j ambiguo, frontera con otros</li>
     *   <li><b>score[j] < 0:</b> Cluster j mal definido, considerar fusión o redivisión</li>
     * </ul>
     * 
     * <p><b>Uso típico:</b></p>
     * <pre>
     * double[] scores = evaluator.silhouettePerCluster(clusters, specs);
     * for (int i = 0; i < scores.length; i++) {
     *     System.out.println("Cluster " + i + ": " + scores[i]);
     *     if (scores[i] < 0.25) {
     *         System.out.println("  ⚠ Cluster mal definido");
     *     }
     * }
     * </pre>
     * 
     * @param clusters Lista de clusters a evaluar
     * @param specs Especificaciones de tipo para cada dimensión de los vectores
     * @return Array donde scores[i] es el Silhouette promedio del cluster i
     * @throws IllegalArgumentException si clusters o specs son null/vacíos
     */
    public double[] silhouettePerCluster(List<Kluster> clusters, DistanceCalculator.FeatureSpec[] specs) {
        if (clusters == null || clusters.isEmpty()) {
            throw new IllegalArgumentException("Clusters cannot be null or empty");
        }
        if (specs == null) {
            throw new IllegalArgumentException("FeatureSpec[] cannot be null");
        }
        
        double[] scores = new double[clusters.size()];
        
        for (int i = 0; i < clusters.size(); i++) {
            Kluster cluster = clusters.get(i);
            List<String[]> members = cluster.getMembers();
            
            if (members.isEmpty()) {
                scores[i] = 0.0;
                continue;
            }
            
            double sumSilhouette = 0.0;
            for (String[] point : members) {
                sumSilhouette += silhouettePoint(point, i, clusters, specs);
            }
            
            scores[i] = sumSilhouette / members.size();
        }
        
        return scores;
    }
}
