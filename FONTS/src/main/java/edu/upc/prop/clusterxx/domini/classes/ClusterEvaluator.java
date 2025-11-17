package edu.upc.prop.clusterxx.domini.classes;

import java.util.List;

/**
 * Evaluador de calidad de clustering mediante coeficiente de Silhouette.
 * 
 * Proporciona métricas para evaluar la calidad de los resultados del clustering,
 * permitiendo comparar diferentes algoritmos o distintos valores de K.
 * 
 * Coeficiente de Silhouette:
 * Métrica que mide qué tan similar es un punto a su propio cluster (cohesión)
 * comparado con otros clusters (separación).
 * 
 * Fórmula por punto: s(i) = (b(i) - a(i)) / max(a(i), b(i))
 * Donde:
 *   - a(i) = (1/|Cᵢ|-1) × Σⱼ∈Cᵢ,ⱼ≠ᵢ d(i,j)
 *           Distancia media intra-cluster (cohesión)
 *   - b(i) = minₖ≠ᵢ [(1/|Cₖ|) × Σⱼ∈Cₖ d(i,j)]
 *           Distancia media al cluster más cercano (separación)
 * 
 * Interpretación de valores:
 *   - s(i) ≈ 1: Punto muy bien asignado
 *   - s(i) ≈ 0: Punto en el borde entre clusters
 *   - s(i) < 0: Punto posiblemente mal asignado
 * 
 * Rango: [-1, 1]
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
     * Esta es la métrica principal para evaluar la calidad global del clustering.
     * Promedia el coeficiente de Silhouette de todos los puntos en todos los clusters.
     * 
     * Casos especiales:
     *   - K = 1 (un solo cluster): Retorna 0.0 (Silhouette no está definido)
     *   - Clusters vacíos: Se ignoran en el cálculo
     * 
     * Interpretación del resultado:
     *   - > 0.7: Clustering muy bueno
     *   - 0.5 - 0.7: Clustering razonable
     *   - 0.25 - 0.5: Clustering débil
     *   - < 0.25: No hay estructura clara
     * 
     * Complejidad: O(N² × K × D)
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
     * Implementa la fórmula: s(i) = (b(i) - a(i)) / max(a(i), b(i))
     * 
     * Donde:
     *   - a(i): Distancia media del punto a todos los demás puntos en su mismo cluster
     *   - b(i): Distancia media mínima del punto a puntos de otros clusters
     * 
     * Casos especiales:
     *   - Cluster con un solo punto: a(i) no está definido → retorna 0.0
     *   - División por cero: retorna 0.0
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
     * Calcula a(i): la distancia media del punto a todos los demás puntos de su mismo cluster.
     * Mide qué tan cohesivo es el cluster (qué tan cerca están sus miembros entre sí).
     * 
     * Si el cluster tiene solo un punto, a(i) no está definido y se retorna Double.NaN.
     * 
     * @param point El punto a evaluar
     * @param clusterIndex Índice del cluster al que pertenece
     * @param clusters Lista de todos los clusters
     * @param specs Especificaciones de tipo para calcular distancias
     * @return Distancia media intra-cluster a(i), o Double.NaN si cluster con 1 solo punto
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
     * Calcula b(i): la distancia media mínima del punto a puntos de otros clusters.
     * Mide qué tan separado está el punto de los clusters vecinos.
     * 
     * Se usa la distancia al cluster más cercano porque queremos saber si el punto
     * está bien asignado respecto a su mejor alternativa.
     * 
     * @param point El punto a evaluar
     * @param clusterIndex Índice del cluster al que pertenece
     * @param clusters Lista de todos los clusters
     * @param specs Especificaciones de tipo para calcular distancias
     * @return Distancia media mínima a otros clusters b(i), o Double.POSITIVE_INFINITY si no hay otros
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
     * Útil para identificar qué clusters están bien definidos y cuáles tienen problemas.
     * Un cluster con Silhouette bajo puede indicar:
     *   - Cluster demasiado disperso (baja cohesión)
     *   - Cluster superpuesto con otros (baja separación)
     *   - K demasiado grande (sobresegmentación)
     * 
     * Interpretación por cluster:
     *   - score[j] > 0.5: Cluster j bien definido
     *   - score[j] ≈ 0: Cluster j ambiguo, frontera con otros
     *   - score[j] < 0: Cluster j mal definido
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
