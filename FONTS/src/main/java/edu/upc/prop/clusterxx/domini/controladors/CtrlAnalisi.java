package edu.upc.prop.clusterxx.domini.controladors;

import edu.upc.prop.clusterxx.domini.classes.*;

import java.util.List;

/**
 * Controlador de análisis para algoritmos de clustering sobre datos heterogéneos.
 * 
 * Proporciona interfaz unificada para ejecutar diferentes algoritmos de clustering
 * sobre datos vectorizados como arrays de String, donde cada dimensión puede representar
 * diferentes tipos de variables (numéricas, ordinales, nominales, texto libre).
 * 
 * Algoritmos soportados:
 *   - KMeans: Inicialización aleatoria de centroides
 *   - KMeans++: Inicialización inteligente (D² sampling)
 *   - KMedoids (PAM): Usa puntos reales como centros, robusto a outliers
 * 
 * Funcionalidades:
 *   - Ejecución de clustering con k especificado
 *   - Búsqueda del k óptimo mediante coeficiente de Silhouette
 *   - Selección aleatoria de k en rango heurístico [2, √n]
 *   - Construcción automática de especificaciones desde objetos Pregunta
 * 
 * @author Sistema de Clustering
 * @version 1.0
 */
public class CtrlAnalisi {

    /**
     * Ejecuta clustering KMeans o KMeans++ sobre datos vectorizados.
     * 
     * Cada punto debe ser un array de String con el mismo número de dimensiones
     * correspondiendo a las especificaciones proporcionadas.
     * 
     * KMeans vs KMeans++:
     *   - KMeans: Inicialización aleatoria, más rápido
     *   - KMeans++: Inicialización inteligente, mejor calidad (recomendado)
     * 
     * @param data Lista de puntos (arrays de String)
     * @param k Número de clusters (2 ≤ k ≤ data.size())
     * @param usePlusPlus true para KMeans++, false para KMeans
     * @param maxIters Máximo de iteraciones (típicamente 100-300)
     * @param specs Especificación de tipos por dimensión
     * @return Lista de k clusters con centroides y miembros
     * @throws IllegalArgumentException si los parámetros son inválidos
     */
    public List<Kluster> cluster(List<String[]> data, int k, boolean usePlusPlus, int maxIters, DistanceCalculator.FeatureSpec[] specs) {
        if (usePlusPlus) return new KMeansPlusPlus().fit(data, k, maxIters, specs);
        return new KMeans().fit(data, k, maxIters, specs);
    }

    /**
     * Ejecuta clustering con el algoritmo especificado por nombre.
     * 
     * Permite seleccionar el algoritmo mediante string, útil cuando el algoritmo
     * se determina dinámicamente (entrada de usuario o configuración).
     * 
     * Algoritmos disponibles (case-insensitive):
     *   - "KMeans": Clásico con inicialización aleatoria
     *   - "KMeans++": Con inicialización inteligente (recomendado)
     *   - "KMedoids" o "K-Medoids": PAM, usa puntos reales como centros
     * 
     * Recomendaciones:
     *   - KMeans++: Mejor opción general (calidad + velocidad)
     *   - KMedoids: Para datos con outliers o centroides interpretables
     *   - KMeans: Solo si necesitas máxima velocidad
     * 
     * @param data Lista de puntos vectorizados
     * @param k Número de clusters
     * @param algorithm Nombre del algoritmo (null → "KMeans")
     * @param maxIters Máximo de iteraciones
     * @param specs Especificaciones de tipo
     * @return Lista de clusters con centroides o medoides
     * @throws IllegalArgumentException si parámetros inválidos
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
     * Método de conveniencia para construir un punto de datos vectorizado.
     * 
     * Facilita la creación de puntos sin crear manualmente arrays de String.
     * Útil en tests o construcción dinámica de datasets.
     * 
     * @param values Valores del punto en orden de dimensiones
     * @return Array de String representando el punto
     */
    public String[] buildPoint(String... values) {
        return values;
    }

    /**
     * Construye especificaciones de características desde preguntas del dominio.
     * 
     * Convierte automáticamente objetos Pregunta en especificaciones para calcular distancias.
     * El orden de las especificaciones corresponde al orden de las preguntas.
     * 
     * Mapeo de tipos:
     *   - NUMERICA → FeatureSpec.numeric(min, max)
     *   - QUALITATIVA_ORDENADA → FeatureSpec.ordinal(orden)
     *   - QUALITATIVA_NO_ORDENADA_SIMPLE → FeatureSpec.nominalSingle()
     *   - QUALITATIVA_NO_ORDENADA_MULTIPLE → FeatureSpec.nominalMultiple()
     *   - TEXT → FeatureSpec.freeText()
     * 
     * @param preguntas Lista de preguntas en orden deseado
     * @return Array de FeatureSpec en el mismo orden
     * @throws IllegalArgumentException si tipo no soportado
     */
    public DistanceCalculator.FeatureSpec[] buildSpecsFromPreguntas(List<Pregunta> preguntas) {
        return FeatureSpecFactory.fromPreguntas(preguntas);
    }

    /**
     * Selecciona un valor de k aleatorio en un rango heurísticamente razonable.
     * 
     * Utiliza la regla heurística [2, √n] donde n es el número de puntos.
     * Útil cuando no hay conocimiento previo sobre la estructura de los datos.
     * 
     * Ejemplos:
     *   - 10 participantes → k ∈ [2, 3]
     *   - 100 participantes → k ∈ [2, 10]
     *   - 400 participantes → k ∈ [2, 20]
     * 
     * @param numParticipants Número de participantes (≥ 2)
     * @return Valor de k seleccionado aleatoriamente en el rango
     * @throws IllegalArgumentException si numParticipants < 2
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
     * Encuentra el k óptimo evaluando con coeficiente de Silhouette.
     * 
     * Prueba todos los valores de k desde kMin hasta kMax y retorna el que
     * maximiza el coeficiente de Silhouette.
     * 
     * @param data Lista de puntos vectorizados
     * @param kMin Valor mínimo de k a evaluar (≥ 2)
     * @param kMax Valor máximo de k a evaluar (≤ data.size())
     * @param algorithm Nombre del algoritmo: "KMeans", "KMeans++", "KMedoids"
     * @param maxIters Máximo de iteraciones por ejecución
     * @param specs Especificaciones de características
     * @return Resultado con mejor k, mejor Silhouette y todos los scores
     * @throws IllegalArgumentException si parámetros inválidos
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
     * Calcula rango recomendado de valores de k para evaluación sistemática.
     * 
     * Proporciona rango que equilibra exhaustividad con eficiencia computacional.
     * 
     * Lógica del rango:
     *   - kMin = 2 (mínimo para clustering)
     *   - kMax = min(10, max(3, √n)) (balance exploración/eficiencia)
     * 
     * Límite de 10: Por encima, la evaluación manual es difícil y el coste
     * computacional de Silhouette crece significativamente (O(n²)).
     * 
     * Ejemplos:
     *   - 5 participantes → [2, 3]
     *   - 25 participantes → [2, 5]
     *   - 100 participantes → [2, 10]
     *   - 500 participantes → [2, 10] (limitado)
     * 
     * @param numParticipants Número de puntos disponibles (≥ 2)
     * @return Array [kMin, kMax] con el rango recomendado
     * @throws IllegalArgumentException si numParticipants < 2
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
     * Resultado de la búsqueda del k óptimo.
     * 
     * Encapsula toda la información generada durante la optimización de k
     * mediante evaluación sistemática con Silhouette.
     * 
     * Contenido:
     *   - bestK: Valor de k que maximizó Silhouette
     *   - bestSilhouette: Mejor coeficiente alcanzado
     *   - kMin, kMax: Rango de valores evaluados
     *   - silhouetteScores[]: Todos los coeficientes calculados
     * 
     * Interpretación de Silhouette:
     *   - > 0.7: Clustering excelente
     *   - 0.5-0.7: Clustering bueno
     *   - 0.25-0.5: Clustering aceptable
     *   - < 0.25: Clustering pobre
     */
    public static class OptimalKResult {
        /** Valor de k que produjo el mejor Silhouette. */
        public final int bestK;
        
        /** Mejor coeficiente de Silhouette encontrado. */
        public final double bestSilhouette;
        
        /** Valor mínimo de k evaluado. */
        public final int kMin;
        
        /** Valor máximo de k evaluado. */
        public final int kMax;
        
        /** Array con todos los Silhouette calculados. */
        public final double[] silhouetteScores;
        
        public OptimalKResult(int bestK, double bestSilhouette, int kMin, int kMax, double[] silhouetteScores) {
            this.bestK = bestK;
            this.bestSilhouette = bestSilhouette;
            this.kMin = kMin;
            this.kMax = kMax;
            this.silhouetteScores = silhouetteScores;
        }
        
        /**
         * Obtiene el coeficiente de Silhouette para un k específico.
         * 
         * Permite consultar el resultado de cualquier k evaluado durante
         * la búsqueda, facilitando análisis comparativo y generación de gráficos.
         * 
         * @param k Valor de k a consultar
         * @return Coeficiente de Silhouette para ese k, o -1.0 si fuera de rango
         */
        public double getSilhouetteForK(int k) {
            if (k < kMin || k > kMax) return -1;
            return silhouetteScores[k - kMin];
        }
    }
}
