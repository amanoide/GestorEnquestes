package edu.upc.prop.clusterxx.domini.classes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Implementación del algoritmo K-Medoids (PAM - Partitioning Around Medoids) para clustering de vectores heterogéneos.
 * 
 * K-Medoids es una variante de K-Means que usa medoides (puntos reales del dataset)
 * como centros de clusters en lugar de centroides calculados (medias).
 * 
 * Diferencias clave con K-Means:
 *   - Centros: K-Medoids usa puntos del dataset; K-Means usa centroides calculados
 *   - Actualización: K-Medoids busca el mejor punto existente; K-Means calcula media
 *   - Distancia: K-Medoids usa Manhattan (L1); K-Means usa Euclidiana (L2)
 *   - Robustez: K-Medoids es más robusto a outliers
 *   - Interpretabilidad: Medoides son puntos reales (más interpretables)
 * 
 * Algoritmo PAM (Partitioning Around Medoids):
 *   1. Inicialización: Seleccionar K puntos del dataset como medoides iniciales
 *   2. Asignación: Asignar cada punto al medoide más cercano (distancia Manhattan)
 *   3. Actualización: Para cada cluster, encontrar el punto que minimiza la suma de distancias
 *      a todos los miembros del cluster (nuevo medoide)
 *   4. Repetir pasos 2-3 hasta convergencia o máximo de iteraciones
 * 
 * Convergencia: El algoritmo converge cuando ni los medoides ni las asignaciones cambian.
 * 
 * Métrica de distancia - ¿Por qué Manhattan (L1)?
 *   - Más robusta a outliers: No eleva las diferencias al cuadrado
 *   - Más eficiente: No requiere raíz cuadrada
 *   - Coherente con la filosofía de K-Medoids de priorizar robustez
 *   - Funciona bien con variables heterogéneas (categóricas y numéricas)
 * 
 * Complejidad: O(I × N² × K × D)
 * Donde I = iteraciones, N = tamaño del dataset (N² por búsqueda de mejor medoide),
 * K = clusters, D = dimensionalidad.
 * Nota: K-Medoids es más costoso que K-Means (O(I × N × K × D)) debido a la búsqueda del mejor medoide.
 * 
 * Manejo de clusters vacíos: Si un cluster queda sin miembros, se reasigna un punto
 * aleatorio del dataset como nuevo medoide.
 * 
 * @see KMeans
 * @see Kluster
 * @see DistanceCalculator
 */
public class KMedoids {
    /** Calculadora de distancias entre vectores heterogéneos. */
    private final DistanceCalculator dc = new DistanceCalculator();
    
    /** Generador de números aleatorios para inicialización y manejo de clusters vacíos. */
    private final Random rnd;

    /**
     * Crea una instancia de K-Medoids con generador aleatorio por defecto.
     * 
     * El generador aleatorio se usa para:
     *   - Selección de medoides iniciales aleatorios en fit
     *   - Reasignación de clusters vacíos durante el entrenamiento
     */
    public KMedoids() { 
        this(new Random()); 
    }
    
    /**
     * Crea una instancia de K-Medoids con generador aleatorio específico.
     * 
     * Útil para reproducibilidad en tests: usar new Random(seed) con semilla fija.
     * 
     * @param rnd Generador de números aleatorios (si es null, se crea uno nuevo)
     */
    public KMedoids(Random rnd) { 
        this.rnd = (rnd == null ? new Random() : rnd); 
    }

    /**
     * Entrena K-Medoids con inicialización aleatoria de medoides.
     * 
     * Proceso:
     *   1. Selecciona K puntos distintos aleatorios del dataset como medoides iniciales
     *   2. Ejecuta el algoritmo PAM usando fitWithInitialMedoids
     * 
     * Nota sobre inicialización: A diferencia de K-Means++, K-Medoids no tiene
     * una estrategia de inicialización estándar ampliamente aceptada. Esta implementación
     * usa selección aleatoria uniforme.
     * 
     * @param data Dataset de vectores heterogéneos (cada elemento es String[])
     * @param k Número de clusters a formar (debe cumplir: 0 < k ≤ |data|)
     * @param maxIters Máximo de iteraciones permitidas (si ≤ 0, se usa 100 por defecto)
     * @param specs Especificaciones de tipo para cada dimensión de los vectores
     * @return Lista de K clusters con sus medoides (puntos reales) y miembros asignados
     * @throws IllegalArgumentException si data es null/vacío, k inválido, o specs es null
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
     * Entrena K-Medoids (PAM) con medoides iniciales proporcionados.
     * 
     * Este es el método principal que implementa el algoritmo PAM completo.
     * 
     * Algoritmo iterativo:
     * Para cada iteración (hasta maxIters):
     *   1. ASIGNACIÓN: Para cada punto x en data:
     *      - Calcular distancia Manhattan a cada medoide
     *      - Asignar x al cluster con medoide más cercano
     *   2. ACTUALIZACIÓN: Para cada cluster c:
     *      - Encontrar todos los puntos asignados a c
     *      - Para cada punto p en c: calcular costo = Σ distancia(p, miembro) para todos los miembros de c
     *      - Seleccionar como nuevo medoide el punto con menor costo
     *   3. MANEJO DE VACÍOS: Si un cluster quedó vacío → asignar punto aleatorio como medoide
     *   4. CONVERGENCIA: Si ni medoides ni asignaciones cambiaron → terminar
     * 
     * Criterio de selección de medoide: El medoide óptimo de un cluster es el punto
     * que minimiza la suma de distancias a todos los demás puntos del cluster. Esto garantiza
     * que el medoide sea representativo y central.
     * 
     * Criterios de parada:
     *   - Convergencia dual: Ni medoides ni asignaciones cambiaron entre iteraciones
     *   - Máximo de iteraciones alcanzado (maxIters)
     * 
     * Diferencia con K-Means: En K-Means, el centroide se calcula como media;
     * en K-Medoids, el medoide se busca entre los puntos existentes evaluando todos los
     * candidatos del cluster.
     * 
     * Complejidad por iteración: O(N × K × D) para asignación + O(K × N² × D)
     * para actualización de medoides = O(K × N² × D) dominante.
     * 
     * @param data Dataset de vectores heterogéneos
     * @param initialMedoidIndices Índices de los puntos del dataset que serán medoides iniciales
     * @param maxIters Máximo de iteraciones permitidas
     * @param specs Especificaciones de tipo para cada dimensión de los vectores
     * @return Lista de K clusters con sus medoides finales y miembros asignados
     * @throws IllegalArgumentException si initialMedoidIndices es null/vacío o specs es null
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
     * 
     * El costo de un medoide es la suma de distancias Manhattan de todos los
     * miembros del cluster a ese medoide. Un costo menor indica un medoide más central
     * y representativo.
     * 
     * Fórmula: costo(m) = Σ d_Manhattan(xᵢ, m) para todo xᵢ en el cluster
     * 
     * Este método se usa para evaluar todos los puntos candidatos de un cluster
     * y seleccionar el que minimiza el costo total.
     * 
     * @param data Dataset completo
     * @param memberIndices Índices de los puntos que pertenecen al cluster
     * @param medoidIdx Índice del punto candidato a evaluar como medoide
     * @param specs Especificaciones de tipo para calcular distancias
     * @return Suma total de distancias Manhattan de todos los miembros al medoide candidato
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
     * Construye la estructura final de clusters a partir de medoides y asignaciones.
     * 
     * Este método crea objetos Kluster usando los medoides finales seleccionados
     * y asigna cada punto del dataset al cluster correspondiente según el array de asignaciones.
     * 
     * Proceso:
     *   1. Crear K clusters vacíos, cada uno con su medoide (copia del punto real)
     *   2. Recorrer todos los puntos del dataset y añadirlos a su cluster asignado
     * 
     * Nota: Los medoides se copian (no se usan referencias) para evitar
     * modificaciones accidentales del dataset original.
     * 
     * @param data Dataset completo
     * @param medoidIdx Array con los índices de los K medoides finales
     * @param assignment Array que mapea cada punto (índice) a su cluster asignado (0 a K-1)
     * @param specs Especificaciones de tipo (no usado aquí, pero mantenido para consistencia)
     * @return Lista de K clusters con medoides como centroides y todos sus miembros asignados
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
     * Selecciona K índices distintos aleatorios del rango [0, n).
     * 
     * Usa un HashSet para garantizar que los índices sean únicos,
     * evitando seleccionar el mismo punto múltiples veces como medoide inicial.
     * 
     * Proceso:
     *   1. Generar K índices aleatorios únicos en [0, n)
     *   2. Convertir el conjunto a lista
     * 
     * Complejidad esperada: O(K) cuando K << n.
     * 
     * @param n Tamaño del rango (número de puntos en el dataset)
     * @param k Número de índices distintos a seleccionar
     * @return Lista de K índices únicos seleccionados aleatoriamente
     */
    private List<Integer> pickDistinctIndices(int n, int k) {
        Set<Integer> indices = new HashSet<>();
        while (indices.size() < k) {
            indices.add(rnd.nextInt(n));
        }
        return new ArrayList<>(indices);
    }
}
