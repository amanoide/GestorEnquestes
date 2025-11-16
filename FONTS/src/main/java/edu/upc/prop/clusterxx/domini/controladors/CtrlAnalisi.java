package edu.upc.prop.clusterxx.domini.controladors;

import edu.upc.prop.clusterxx.domini.classes.*;

import java.util.List;

/**
 * Controlador de análisis para clustering de usuarios por respuestas (todos String).
 * Requiere FeatureSpec[] para calcular distancias por dimensión según tipo de variable.
 */
public class CtrlAnalisi {

    /**
     * Ejecuta clustering KMeans o KMeans++ sobre datos vectorizados (String[]).
     * @param data Lista de puntos, cada punto es un array de String
     * @param k número de clústeres
     * @param usePlusPlus true para inicialización KMeans++
     * @param maxIters máximo de iteraciones
     * @param specs especificación por dimensión del tipo de variable
     * @return lista de clústeres con centroides y miembros
     */
    public List<Kluster> cluster(List<String[]> data, int k, boolean usePlusPlus, int maxIters, DistanceCalculator.FeatureSpec[] specs) {
        if (usePlusPlus) return new KMeansPlusPlus().fit(data, k, maxIters, specs);
        return new KMeans().fit(data, k, maxIters, specs);
    }

    /**
     * Ejecuta clustering con el algoritmo especificado.
     * @param data Lista de puntos, cada punto es un array de String
     * @param k número de clústeres
     * @param algorithm nombre del algoritmo: "KMeans", "KMeans++", "KMedoids"
     * @param maxIters máximo de iteraciones
     * @param specs especificación por dimensión del tipo de variable
     * @return lista de clústeres con centroides/medoides y miembros
     */
    public List<Kluster> clusterWithAlgorithm(List<String[]> data, int k, String algorithm, int maxIters, DistanceCalculator.FeatureSpec[] specs) {
        if (algorithm == null) algorithm = "KMeans";
        
        switch (algorithm.toLowerCase()) {
            case "kmeans++":
                return new KMeansPlusPlus().fit(data, k, maxIters, specs);
            case "kmedoids":
            case "k-medoids":
                return new KMedoids().fit(data, k, maxIters, specs);
            case "kmeans":
            default:
                return new KMeans().fit(data, k, maxIters, specs);
        }
    }

    /**
     * Helper para construir un punto a partir de valores String.
     * Todos los valores deben ser String.
     */
    public String[] buildPoint(String... values) {
        return values;
    }

    /** Construye los FeatureSpec[] desde una lista de Preguntas del dominio (mismo orden). */
    public DistanceCalculator.FeatureSpec[] buildSpecsFromPreguntas(List<Pregunta> preguntas) {
        return FeatureSpecFactory.fromPreguntas(preguntas);
    }

    /**
     * Selecciona un valor de k aleatorio dentro de un rango razonable.
     * El rango va de 2 hasta la raíz cuadrada del número de participantes.
     * 
     * @param numParticipants Número total de participantes/puntos de datos
     * @return Valor de k seleccionado aleatoriamente
     * @throws IllegalArgumentException Si numParticipants < 2
     */
    public int selectRandomK(int numParticipants) {
        if (numParticipants < 2) {
            throw new IllegalArgumentException("Se necesitan al menos 2 participantes para clustering");
        }
        
        int kMax = (int) Math.sqrt(numParticipants);
        if (kMax < 2) kMax = 2;
        
        java.util.Random random = new java.util.Random();
        return 2 + random.nextInt(Math.max(1, kMax - 1));
    }

    /**
     * Encuentra el valor óptimo de k evaluando diferentes valores con el coeficiente de Silhouette.
     * Prueba valores de k desde kMin hasta kMax y retorna el que maximiza el Silhouette.
     * 
     * @param data Lista de puntos vectorizados
     * @param kMin Valor mínimo de k a evaluar (mínimo 2)
     * @param kMax Valor máximo de k a evaluar
     * @param algorithm Nombre del algoritmo: "KMeans", "KMeans++", "KMedoids"
     * @param maxIters Máximo de iteraciones por ejecución
     * @param specs Especificación de características por dimensión
     * @return Resultado con el mejor k, mejor Silhouette y todos los scores evaluados
     * @throws IllegalArgumentException Si los parámetros son inválidos
     */
    public OptimalKResult findOptimalK(List<String[]> data, int kMin, int kMax, 
                                        String algorithm, int maxIters, 
                                        DistanceCalculator.FeatureSpec[] specs) {
        // Validaciones
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("Los datos no pueden estar vacíos");
        }
        if (kMin < 2) {
            throw new IllegalArgumentException("kMin debe ser al menos 2");
        }
        if (kMax < kMin) {
            throw new IllegalArgumentException("kMax debe ser mayor o igual que kMin");
        }
        if (kMax > data.size()) {
            throw new IllegalArgumentException("kMax no puede ser mayor que el número de participantes");
        }
        
        double bestSilhouette = -1;
        int bestK = kMin;
        double[] silhouetteScores = new double[kMax - kMin + 1];
        
        ClusterEvaluator evaluator = new ClusterEvaluator();
        
        // Evaluar cada valor de k
        for (int k = kMin; k <= kMax; k++) {
            List<Kluster> clusters = clusterWithAlgorithm(data, k, algorithm, maxIters, specs);
            double silhouette = evaluator.silhouetteScore(clusters, specs);
            
            silhouetteScores[k - kMin] = silhouette;
            
            if (silhouette > bestSilhouette) {
                bestSilhouette = silhouette;
                bestK = k;
            }
        }
        
        return new OptimalKResult(bestK, bestSilhouette, kMin, kMax, silhouetteScores);
    }

    /**
     * Calcula un rango razonable de k para evaluar basado en el número de participantes.
     * Usa la regla de √n como máximo, con un límite superior de 10 para eficiencia.
     * 
     * @param numParticipants Número de participantes
     * @return Array [kMin, kMax] con el rango recomendado
     */
    public int[] suggestKRange(int numParticipants) {
        if (numParticipants < 2) {
            throw new IllegalArgumentException("Se necesitan al menos 2 participantes");
        }
        
        int kMin = 2;
        int kMax = Math.min(10, Math.max(3, (int) Math.sqrt(numParticipants)));
        
        return new int[]{kMin, kMax};
    }

    /**
     * Clase para encapsular el resultado de la búsqueda del k óptimo.
     */
    public static class OptimalKResult {
        public final int bestK;
        public final double bestSilhouette;
        public final int kMin;
        public final int kMax;
        public final double[] silhouetteScores; // Índice i corresponde a k = kMin + i
        
        public OptimalKResult(int bestK, double bestSilhouette, int kMin, int kMax, double[] silhouetteScores) {
            this.bestK = bestK;
            this.bestSilhouette = bestSilhouette;
            this.kMin = kMin;
            this.kMax = kMax;
            this.silhouetteScores = silhouetteScores;
        }
        
        /**
         * Obtiene el score de Silhouette para un k específico.
         * @param k Valor de k
         * @return Score de Silhouette, o -1 si k está fuera del rango evaluado
         */
        public double getSilhouetteForK(int k) {
            if (k < kMin || k > kMax) return -1;
            return silhouetteScores[k - kMin];
        }
    }
}
