package edu.upc.prop.clusterxx.domini.classes;

import java.util.List;

/**
 * Evaluador de calidad de clustering.
 * Proporciona métricas para evaluar la calidad de los resultados del clustering.
 */
public class ClusterEvaluator {
    private final DistanceCalculator dc = new DistanceCalculator();

    /**
     * Calcula el coeficiente de Silhouette promedio para todo el clustering.
     * 
     * El coeficiente de Silhouette mide qué tan similar es un punto a su propio cluster
     * comparado con otros clusters.
     * 
     * Valores:
     * - Cercano a 1: puntos bien asignados, clusters compactos y separados
     * - Cercano a 0: puntos en el borde entre clusters
     * - Negativo: puntos posiblemente mal asignados
     * 
     * Rango: [-1, 1]
     * 
     * @param clusters Lista de clusters resultantes del algoritmo
     * @param specs Especificación de tipos de variables por dimensión
     * @return Coeficiente de Silhouette promedio
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
     * Fórmula: s(i) = (b(i) - a(i)) / max(a(i), b(i))
     * 
     * Donde:
     * - a(i) = distancia media del punto a todos los demás puntos en su mismo cluster
     * - b(i) = distancia media mínima del punto a puntos de otros clusters
     * 
     * @param point El punto a evaluar
     * @param clusterIndex Índice del cluster al que pertenece el punto
     * @param clusters Lista de todos los clusters
     * @param specs Especificación de tipos de variables
     * @return Coeficiente de Silhouette del punto
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
     * Calcula la distancia media del punto a los demás puntos de su mismo cluster.
     * 
     * @param point El punto a evaluar
     * @param clusterIndex Índice del cluster al que pertenece
     * @param clusters Lista de todos los clusters
     * @param specs Especificación de tipos de variables
     * @return Distancia media intra-cluster (a(i))
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
     * Calcula la distancia media mínima del punto a puntos de otros clusters.
     * 
     * Para cada cluster diferente al propio, calcula la distancia media del punto
     * a todos los puntos de ese cluster, y devuelve el mínimo.
     * 
     * @param point El punto a evaluar
     * @param clusterIndex Índice del cluster al que pertenece
     * @param clusters Lista de todos los clusters
     * @param specs Especificación de tipos de variables
     * @return Distancia media mínima a otros clusters (b(i))
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
     * Calcula el coeficiente de Silhouette por cluster.
     * Útil para identificar qué clusters están bien definidos y cuáles no.
     * 
     * @param clusters Lista de clusters
     * @param specs Especificación de tipos de variables
     * @return Array con el Silhouette promedio de cada cluster
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
