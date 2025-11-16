package edu.upc.prop.clusterxx.domini.controladors;

import edu.upc.prop.clusterxx.domini.classes.*;

import java.util.List;

/**
 * Controlador de análisis para algoritmos de clustering sobre datos heterogéneos.
 * 
 * <p>Esta clase proporciona una interfaz unificada para ejecutar diferentes algoritmos
 * de clustering sobre datos vectorizados como arrays de String, donde cada dimensión
 * puede representar diferentes tipos de variables (numéricas, ordinales, nominales, texto libre).</p>
 * 
 * <p><b>Algoritmos soportados:</b></p>
 * <ul>
 *   <li><b>KMeans:</b> Algoritmo clásico con inicialización aleatoria de centroides</li>
 *   <li><b>KMeans++:</b> Mejora de KMeans con inicialización inteligente (D² sampling)</li>
 *   <li><b>KMedoids (PAM):</b> Usa puntos reales como centros, robusto a outliers</li>
 * </ul>
 * 
 * <p><b>Características principales:</b></p>
 * <ul>
 *   <li>Manejo de datos heterogéneos mediante {@link DistanceCalculator.FeatureSpec}</li>
 *   <li>Selección automática y aleatoria del número de clusters (k)</li>
 *   <li>Optimización de k mediante evaluación con coeficiente de Silhouette</li>
 *   <li>Construcción automática de especificaciones desde objetos del dominio</li>
 * </ul>
 * 
 * <p><b>Flujo típico de uso:</b></p>
 * <pre>
 * // 1. Preparar datos
 * List&lt;String[]&gt; data = Arrays.asList(
 *     new String[]{"25", "Alto", "Soltero"},
 *     new String[]{"30", "Medio", "Casado"}
 * );
 * 
 * // 2. Definir tipos de variables
 * FeatureSpec[] specs = {
 *     FeatureSpec.numeric(0.0, 100.0),           // Edad
 *     FeatureSpec.ordinal(Arrays.asList("Bajo", "Medio", "Alto")), // Ingresos
 *     FeatureSpec.nominalSingle()                // Estado civil
 * };
 * 
 * // 3. Ejecutar clustering
 * CtrlAnalisi ctrl = new CtrlAnalisi();
 * List&lt;Kluster&gt; clusters = ctrl.clusterWithAlgorithm(data, 3, "KMeans++", 100, specs);
 * 
 * // 4. O encontrar k óptimo automáticamente
 * OptimalKResult result = ctrl.findOptimalK(data, 2, 8, "KMeans++", 100, specs);
 * int bestK = result.bestK;
 * </pre>
 * 
 * <p><b>Integración con el dominio:</b></p>
 * <p>La clase puede construir automáticamente las especificaciones de características
 * desde objetos {@link Pregunta} del dominio de la aplicación usando
 * {@link #buildSpecsFromPreguntas(List)}.</p>
 * 
 * <p><b>Selección de k:</b></p>
 * <ul>
 *   <li>{@link #selectRandomK(int)}: Selección aleatoria en rango [2, √n]</li>
 *   <li>{@link #findOptimalK}: Búsqueda exhaustiva maximizando Silhouette</li>
 *   <li>{@link #suggestKRange(int)}: Cálculo de rango recomendado</li>
 * </ul>
 * 
 * <p><b>Complejidad computacional:</b></p>
 * <ul>
 *   <li><b>KMeans/KMeans++:</b> O(I × N × K × D) donde I=iteraciones, N=puntos, K=clusters, D=dimensiones</li>
 *   <li><b>KMedoids:</b> O(I × N² × K × D) - más costoso pero más robusto</li>
 *   <li><b>findOptimalK:</b> O((kMax-kMin) × complejidad del algoritmo)</li>
 * </ul>
 * 
 * <p><b>Consideraciones de rendimiento:</b></p>
 * <ul>
 *   <li>Para datasets grandes (&gt;1000 puntos), preferir KMeans++ sobre KMedoids</li>
 *   <li>Limitar el rango de búsqueda de k óptimo (recomendado: máximo 10)</li>
 *   <li>El cálculo de Silhouette es O(N²), costoso para muchos puntos</li>
 * </ul>
 * 
 * @author Sistema de Clustering
 * @version 1.0
 * @see KMeans
 * @see KMeansPlusPlus
 * @see KMedoids
 * @see ClusterEvaluator
 * @see DistanceCalculator.FeatureSpec
 */
public class CtrlAnalisi {

    /**
     * Ejecuta clustering KMeans o KMeans++ sobre datos vectorizados.
     * 
     * <p>Aplica el algoritmo de clustering especificado sobre los datos proporcionados.
     * Cada punto debe ser un array de String con el mismo número de dimensiones,
     * correspondiendo a las especificaciones proporcionadas.</p>
     * 
     * <p><b>KMeans vs KMeans++:</b></p>
     * <ul>
     *   <li><b>KMeans:</b> Inicialización aleatoria de centroides, más rápido</li>
     *   <li><b>KMeans++:</b> Inicialización inteligente D² sampling, mejor calidad</li>
     * </ul>
     * 
     * @param data Lista de puntos, cada punto es un array de String de longitud igual a specs.length
     * @param k Número de clusters a generar (debe ser ≥ 2 y ≤ data.size())
     * @param usePlusPlus true para usar KMeans++ (recomendado), false para KMeans estándar
     * @param maxIters Máximo número de iteraciones (típicamente 100-300)
     * @param specs Especificación del tipo de variable por dimensión (longitud = data[0].length)
     * @return Lista de k clusters, cada uno con su centroide y miembros asignados
     * @throws IllegalArgumentException Si los parámetros son inválidos o incompatibles
     * @see KMeans#fit(List, int, int, DistanceCalculator.FeatureSpec[])
     * @see KMeansPlusPlus#fit(List, int, int, DistanceCalculator.FeatureSpec[])
     */
    public List<Kluster> cluster(List<String[]> data, int k, boolean usePlusPlus, int maxIters, DistanceCalculator.FeatureSpec[] specs) {
        if (usePlusPlus) return new KMeansPlusPlus().fit(data, k, maxIters, specs);
        return new KMeans().fit(data, k, maxIters, specs);
    }

    /**
     * Ejecuta clustering con el algoritmo especificado por nombre.
     * 
     * <p>Método de conveniencia que permite seleccionar el algoritmo mediante un string.
     * Es especialmente útil cuando el algoritmo se determina dinámicamente (por ejemplo,
     * desde entrada del usuario o configuración).</p>
     * 
     * <p><b>Algoritmos disponibles:</b></p>
     * <ul>
     *   <li><b>"KMeans":</b> K-Means clásico con inicialización aleatoria</li>
     *   <li><b>"KMeans++":</b> K-Means con inicialización inteligente (recomendado)</li>
     *   <li><b>"KMedoids" o "K-Medoids":</b> PAM algorithm, usa puntos reales como centros</li>
     * </ul>
     * 
     * <p><b>Recomendaciones de uso:</b></p>
     * <ul>
     *   <li><b>KMeans++:</b> Mejor opción general (buena calidad + velocidad)</li>
     *   <li><b>KMedoids:</b> Para datos con outliers o cuando necesites centroides interpretables</li>
     *   <li><b>KMeans:</b> Solo si necesitas máxima velocidad y los datos están bien distribuidos</li>
     * </ul>
     * 
     * @param data Lista de puntos vectorizados como arrays de String
     * @param k Número de clusters deseado
     * @param algorithm Nombre del algoritmo (case-insensitive). Si es null o inválido, usa "KMeans"
     * @param maxIters Máximo de iteraciones para convergencia
     * @param specs Especificaciones de tipo por dimensión
     * @return Lista de clusters con centroides (KMeans/KMeans++) o medoides (KMedoids)
     * @throws IllegalArgumentException Si los parámetros numéricos son inválidos
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
     * <p>Facilita la creación de puntos de datos para clustering sin necesidad
     * de crear manualmente arrays de String. Especialmente útil en tests
     * o construcción dinámica de datasets.</p>
     * 
     * <p><b>Ejemplo de uso:</b></p>
     * <pre>
     * CtrlAnalisi ctrl = new CtrlAnalisi();
     * String[] punto1 = ctrl.buildPoint("25", "Alto", "Barcelona");
     * String[] punto2 = ctrl.buildPoint("30", "Medio", "Madrid");
     * List&lt;String[]&gt; data = Arrays.asList(punto1, punto2);
     * </pre>
     * 
     * @param values Valores del punto en orden de las dimensiones. Todos deben ser String
     * @return Array de String representando el punto vectorizado
     * @throws NullPointerException Si algún valor es null (usar "" para valores vacíos)
     */
    public String[] buildPoint(String... values) {
        return values;
    }

    /**
     * Construye las especificaciones de características desde preguntas del dominio.
     * 
     * <p>Convierte automáticamente objetos {@link Pregunta} del dominio de la aplicación
     * en especificaciones que el algoritmo de clustering puede usar para calcular distancias.
     * El orden de las especificaciones corresponde exactamente al orden de las preguntas.</p>
     * 
     * <p><b>Mapeo de tipos:</b></p>
     * <ul>
     *   <li><b>NUMERICA:</b> → FeatureSpec.numeric(min, max)</li>
     *   <li><b>QUALITATIVA_ORDENADA:</b> → FeatureSpec.ordinal(orden)</li>
     *   <li><b>QUALITATIVA_NO_ORDENADA_SIMPLE:</b> → FeatureSpec.nominalSingle()</li>
     *   <li><b>QUALITATIVA_NO_ORDENADA_MULTIPLE:</b> → FeatureSpec.nominalMultiple()</li>
     *   <li><b>TEXT:</b> → FeatureSpec.freeText()</li>
     * </ul>
     * 
     * @param preguntas Lista de preguntas del dominio en el orden deseado para vectorización
     * @return Array de FeatureSpec en el mismo orden que las preguntas
     * @throws IllegalArgumentException Si alguna pregunta tiene tipo no soportado
     * @see FeatureSpecFactory#fromPreguntas(List)
     */
    public DistanceCalculator.FeatureSpec[] buildSpecsFromPreguntas(List<Pregunta> preguntas) {
        return FeatureSpecFactory.fromPreguntas(preguntas);
    }

    /**
     * Selecciona un valor de k aleatorio dentro de un rango heurísticamente razonable.
     * 
     * <p>Utiliza la regla heurística de que un número apropiado de clusters está
     * en el rango [2, √n], donde n es el número de puntos de datos. Esta aproximación
     * es útil cuando no se tiene conocimiento previo sobre la estructura de los datos.</p>
     * 
     * <p><b>Fórmula:</b> k ∈ [2, √numParticipants]</p>
     * 
     * <p><b>Ejemplos:</b></p>
     * <ul>
     *   <li>10 participantes → k ∈ [2, 3]</li>
     *   <li>100 participantes → k ∈ [2, 10]</li>
     *   <li>400 participantes → k ∈ [2, 20]</li>
     * </ul>
     * 
     * <p><b>Casos de uso:</b></p>
     * <ul>
     *   <li>Clustering exploratorio sin conocimiento previo</li>
     *   <li>Algoritmos que requieren múltiples ejecuciones con k diferente</li>
     *   <li>Benchmarking y comparación de algoritmos</li>
     * </ul>
     * 
     * @param numParticipants Número total de participantes/puntos de datos (≥ 2)
     * @return Valor de k seleccionado uniformemente al azar en el rango válido
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
     * Calcula un rango recomendado de valores de k para evaluación sistemática.
     * 
     * <p>Proporciona un rango de valores de k que equilibra exhaustividad de búsqueda
     * con eficiencia computacional. Se basa en heurísticas establecidas en la literatura
     * de clustering y considera limitaciones prácticas de tiempo de cómputo.</p>
     * 
     * <p><b>Lógica del rango:</b></p>
     * <ul>
     *   <li><b>kMin = 2:</b> Mínimo meaningful para clustering</li>
     *   <li><b>kMax = min(10, max(3, √n)):</b> Balance entre exploración y eficiencia</li>
     * </ul>
     * 
     * <p><b>Límite de 10:</b> Por encima de 10 clusters, la evaluación manual se vuelve
     * difícil y el coste computacional de Silhouette crece significativamente (O(n²)).</p>
     * 
     * <p><b>Ejemplos de rangos:</b></p>
     * <ul>
     *   <li>5 participantes → [2, 3]</li>
     *   <li>25 participantes → [2, 5]</li>
     *   <li>100 participantes → [2, 10]</li>
     *   <li>500 participantes → [2, 10] (limitado)</li>
     * </ul>
     * 
     * @param numParticipants Número de puntos de datos disponibles (≥ 2)
     * @return Array de dos elementos [kMin, kMax] representando el rango recomendado
     * @throws IllegalArgumentException Si numParticipants < 2
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
     * Resultado de la búsqueda del número óptimo de clusters (k).
     * 
     * <p>Encapsula toda la información generada durante el proceso de optimización
     * de k mediante evaluación sistemática con el coeficiente de Silhouette.
     * Permite analizar no solo el mejor k encontrado, sino también examinar
     * la calidad de todos los valores evaluados.</p>
     * 
     * <p><b>Información contenida:</b></p>
     * <ul>
     *   <li><b>bestK:</b> Valor de k que maximizó el coeficiente de Silhouette</li>
     *   <li><b>bestSilhouette:</b> Mejor coeficiente de Silhouette alcanzado</li>
     *   <li><b>kMin, kMax:</b> Rango de valores evaluados</li>
     *   <li><b>silhouetteScores[]:</b> Todos los coeficientes calculados para análisis</li>
     * </ul>
     * 
     * <p><b>Ejemplo de uso:</b></p>
     * <pre>
     * OptimalKResult result = ctrl.findOptimalK(data, 2, 8, "KMeans++", 100, specs);
     * 
     * System.out.println("Mejor k: " + result.bestK);
     * System.out.println("Silhouette: " + result.bestSilhouette);
     * 
     * // Analizar todos los valores
     * for (int k = result.kMin; k &lt;= result.kMax; k++) {
     *     double score = result.getSilhouetteForK(k);
     *     System.out.printf("k=%d → Silhouette=%.3f%n", k, score);
     * }
     * </pre>
     * 
     * <p><b>Interpretación de Silhouette:</b></p>
     * <ul>
     *   <li><b>&gt; 0.7:</b> Clustering excelente</li>
     *   <li><b>0.5-0.7:</b> Clustering bueno</li>
     *   <li><b>0.25-0.5:</b> Clustering aceptable</li>
     *   <li><b>&lt; 0.25:</b> Clustering pobre</li>
     * </ul>
     * 
     * @see CtrlAnalisi#findOptimalK(List, int, int, String, int, DistanceCalculator.FeatureSpec[])
     */
    public static class OptimalKResult {
        /** Valor de k que produjo el mejor coeficiente de Silhouette. */
        public final int bestK;
        
        /** Mejor coeficiente de Silhouette encontrado (correspondiente a bestK). */
        public final double bestSilhouette;
        
        /** Valor mínimo de k evaluado (inclusive). */
        public final int kMin;
        
        /** Valor máximo de k evaluado (inclusive). */
        public final int kMax;
        
        /** 
         * Array con todos los coeficientes de Silhouette calculados.
         * El índice i corresponde a k = kMin + i.
         * Longitud: kMax - kMin + 1
         */
        public final double[] silhouetteScores;
        
        public OptimalKResult(int bestK, double bestSilhouette, int kMin, int kMax, double[] silhouetteScores) {
            this.bestK = bestK;
            this.bestSilhouette = bestSilhouette;
            this.kMin = kMin;
            this.kMax = kMax;
            this.silhouetteScores = silhouetteScores;
        }
        
        /**
         * Obtiene el coeficiente de Silhouette para un valor específico de k.
         * 
         * <p>Permite consultar el resultado de cualquier valor de k que fue evaluado
         * durante la búsqueda de optimización, facilitando el análisis comparativo
         * y la generación de gráficos de calidad vs. número de clusters.</p>
         * 
         * @param k Valor de k del cual se quiere obtener el Silhouette
         * @return Coeficiente de Silhouette para ese k, o -1.0 si k está fuera del rango [kMin, kMax]
         * @see ClusterEvaluator#silhouetteScore(List, DistanceCalculator.FeatureSpec[])
         */
        public double getSilhouetteForK(int k) {
            if (k < kMin || k > kMax) return -1;
            return silhouetteScores[k - kMin];
        }
    }
}
