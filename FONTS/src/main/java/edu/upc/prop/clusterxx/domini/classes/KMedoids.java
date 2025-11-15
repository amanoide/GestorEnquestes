package edu.upc.prop.clusterxx.domini.classes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Implementación de K-Medoids (PAM - Partitioning Around Medoids).
 * A diferencia de K-Means que usa centroides calculados (medias), K-Medoids 
 * usa medoides reales (puntos del dataset) como centros de los clusters.
 * 
 * Ventajas sobre K-Means:
 * - Más robusto frente a outliers
 * - Los medoides son puntos reales del dataset (más interpretables)
 * - Puede funcionar con cualquier función de distancia
 * 
 * NOTA: Utiliza distancia Manhattan (L1) en lugar de Euclidiana (L2) porque:
 * - Manhattan es más robusta a outliers (no eleva diferencias al cuadrado)
 * - Más eficiente computacionalmente (sin raíz cuadrada)
 * - Coherente con la filosofía de K-Medoids de priorizar robustez
 */
public class KMedoids {
    private final DistanceCalculator dc = new DistanceCalculator();
    private final Random rnd;

    public KMedoids() { 
        this(new Random()); 
    }
    
    public KMedoids(Random rnd) { 
        this.rnd = (rnd == null ? new Random() : rnd); 
    }

    /**
     * Ejecuta K-Medoids con inicialización aleatoria.
     * @param data Lista de puntos (cada punto es un String[])
     * @param k Número de clusters
     * @param maxIters Máximo de iteraciones
     * @param specs Especificación de tipos de variables por dimensión
     * @return Lista de clusters con medoides y miembros
     */
    public List<Kluster> fit(List<String[]> data, int k, int maxIters, DistanceCalculator.FeatureSpec[] specs) {
        if (data == null || data.isEmpty()) 
            throw new IllegalArgumentException("data empty");
        if (k <= 0 || k > data.size()) 
            throw new IllegalArgumentException("invalid k");
        if (maxIters <= 0) maxIters = 100;
        if (specs == null) 
            throw new IllegalArgumentException("specs required");

        // Inicialización: seleccionar k puntos distintos del dataset como medoides iniciales
        List<Integer> medoidIndices = pickDistinctIndices(data.size(), k);
        return fitWithInitialMedoids(data, medoidIndices, maxIters, specs);
    }

    /**
     * Ejecuta K-Medoids con medoides iniciales dados.
     * @param data Lista de puntos
     * @param initialMedoidIndices Índices de los puntos del dataset que serán medoides iniciales
     * @param maxIters Máximo de iteraciones
     * @param specs Especificación de tipos de variables
     * @return Lista de clusters
     */
    public List<Kluster> fitWithInitialMedoids(List<String[]> data, List<Integer> initialMedoidIndices, 
                                                int maxIters, DistanceCalculator.FeatureSpec[] specs) {
        if (initialMedoidIndices == null || initialMedoidIndices.isEmpty()) 
            throw new IllegalArgumentException("no initial medoids");
        if (specs == null) 
            throw new IllegalArgumentException("specs required");
        
        int k = initialMedoidIndices.size();
        
        // Índices actuales de los medoides
        int[] medoidIdx = new int[k];
        for (int i = 0; i < k; i++) {
            medoidIdx[i] = initialMedoidIndices.get(i);
        }

        // Asignación de puntos a clusters (índice del cluster para cada punto)
        int[] assignment = new int[data.size()];
        
        for (int iter = 0; iter < maxIters; iter++) {
            // Paso 1: Asignar cada punto al medoide más cercano
            boolean assignmentChanged = false;
            for (int i = 0; i < data.size(); i++) {
                int bestCluster = 0;
                double bestDist = Double.POSITIVE_INFINITY;
                
                for (int j = 0; j < k; j++) {
                    double dist = dc.distanceManhattan(data.get(i), data.get(medoidIdx[j]), specs);
                    if (dist < bestDist) {
                        bestDist = dist;
                        bestCluster = j;
                    }
                }
                
                if (assignment[i] != bestCluster) {
                    assignment[i] = bestCluster;
                    assignmentChanged = true;
                }
            }

            // Paso 2: Para cada cluster, encontrar el mejor medoide
            // (el punto que minimiza la suma de distancias a todos los miembros del cluster)
            boolean medoidChanged = false;
            
            for (int clusterIdx = 0; clusterIdx < k; clusterIdx++) {
                // Encontrar todos los puntos asignados a este cluster
                List<Integer> clusterMembers = new ArrayList<>();
                for (int i = 0; i < data.size(); i++) {
                    if (assignment[i] == clusterIdx) {
                        clusterMembers.add(i);
                    }
                }
                
                // Si el cluster está vacío, reasignar un punto aleatorio
                if (clusterMembers.isEmpty()) {
                    int newMedoid = rnd.nextInt(data.size());
                    if (medoidIdx[clusterIdx] != newMedoid) {
                        medoidIdx[clusterIdx] = newMedoid;
                        medoidChanged = true;
                    }
                    continue;
                }
                
                // Encontrar el punto del cluster que minimiza la suma de distancias
                int bestMedoid = medoidIdx[clusterIdx];
                double bestCost = computeClusterCost(data, clusterMembers, bestMedoid, specs);
                
                for (int candidateIdx : clusterMembers) {
                    double cost = computeClusterCost(data, clusterMembers, candidateIdx, specs);
                    if (cost < bestCost) {
                        bestCost = cost;
                        bestMedoid = candidateIdx;
                    }
                }
                
                if (medoidIdx[clusterIdx] != bestMedoid) {
                    medoidIdx[clusterIdx] = bestMedoid;
                    medoidChanged = true;
                }
            }

            // Si ni las asignaciones ni los medoides cambiaron, hemos convergido
            if (!assignmentChanged && !medoidChanged) {
                break;
            }
        }

        // Construir la lista de Kluster a partir de los medoides finales
        return buildClusters(data, medoidIdx, assignment, specs);
    }

    /**
     * Calcula el costo de un cluster dado un medoide candidato.
     * El costo es la suma de distancias Manhattan de todos los miembros al medoide.
     */
    private double computeClusterCost(List<String[]> data, List<Integer> memberIndices, 
                                     int medoidIdx, DistanceCalculator.FeatureSpec[] specs) {
        double cost = 0.0;
        String[] medoid = data.get(medoidIdx);
        for (int memberIdx : memberIndices) {
            cost += dc.distanceManhattan(data.get(memberIdx), medoid, specs);
        }
        return cost;
    }

    /**
     * Construye los clusters finales a partir de los medoides y asignaciones.
     */
    private List<Kluster> buildClusters(List<String[]> data, int[] medoidIdx, 
                                       int[] assignment, DistanceCalculator.FeatureSpec[] specs) {
        int k = medoidIdx.length;
        List<Kluster> clusters = new ArrayList<>(k);
        
        // Crear un Kluster para cada medoide
        for (int i = 0; i < k; i++) {
            String[] medoid = data.get(medoidIdx[i]);
            clusters.add(new Kluster(Arrays.copyOf(medoid, medoid.length)));
        }
        
        // Asignar los miembros a cada cluster
        for (int i = 0; i < data.size(); i++) {
            int clusterIdx = assignment[i];
            clusters.get(clusterIdx).addMember(data.get(i));
        }
        
        return clusters;
    }

    /**
     * Selecciona k índices distintos aleatorios del rango [0, n).
     */
    private List<Integer> pickDistinctIndices(int n, int k) {
        Set<Integer> indices = new HashSet<>();
        while (indices.size() < k) {
            indices.add(rnd.nextInt(n));
        }
        return new ArrayList<>(indices);
    }
}
