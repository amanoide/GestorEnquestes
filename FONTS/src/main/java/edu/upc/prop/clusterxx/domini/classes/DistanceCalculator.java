package edu.upc.prop.clusterxx.domini.classes;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Calculadora de distancias entre vectores heterogéneos.
 * 
 * Permite calcular distancias entre vectores que contienen diferentes tipos
 * de variables (numéricas, ordinales, nominales, texto libre).
 * 
 * Métricas soportadas:
 *   - Distancia Euclidiana (L2): √(Σd²ᵢ) / N - Recomendada para K-Means y K-Means++
 *   - Distancia Manhattan (L1): Σ(dᵢ/N) - Recomendada para K-Medoids (más robusta a outliers)
 * 
 * La distancia se calcula combinando distancias locales específicas para cada tipo de
 * variable, permitiendo el análisis de datos heterogéneos de forma consistente.
 * 
 * @author ClusterXX Team
 * @version 2.0
 */
public class DistanceCalculator {

    /**
     * Tipos de variables soportadas para el cálculo de distancia local.
     * 
     * Cada tipo de variable tiene asociada una función de distancia específica
     * que normaliza adecuadamente las diferencias entre valores.
     */
    public enum VariableKind {
        /** Variable cuantitativa: representa un valor numérico continuo o discreto */
        NUMERIC,
        
        /** Variable cualitativa ordenada: modalidades con un orden predefinido (ej: bajo, medio, alto) */
        ORDINAL,
        
        /** Variable cualitativa no ordenada: un único valor de un conjunto de categorías */
        NOMINAL_SINGLE,
        
        /** Variable cualitativa no ordenada múltiple: conjunto de valores seleccionados */
        NOMINAL_MULTI,
        
        /** Variable de texto libre: cadena de caracteres sin estructura predefinida */
        FREE_TEXT
    }

    /**
     * Especificación de características para una dimensión del vector.
     * 
     * Define los metadatos necesarios para calcular la distancia local en una dimensión
     * específica del vector de datos. Dependiendo del tipo de variable (kind), se utilizan
     * diferentes campos opcionales:
     * 
     *   - NUMERIC: Usa numericMin y numericMax para normalizar
     *   - ORDINAL: Usa ordinalOrder y ordinalCardinality para calcular posiciones
     *   - NOMINAL_SINGLE/MULTI: No requiere metadatos adicionales
     *   - FREE_TEXT: No requiere metadatos adicionales
     * 
     * @see VariableKind
     */
    public static class FeatureSpec {
        /** Tipo de variable para esta dimensión */
        public final VariableKind kind;
        
        /** Lista ordenada de modalidades para variables ORDINAL (obligatorio para ORDINAL) */
        public final List<String> ordinalOrder;
        
        /** Número de modalidades para variables ORDINAL (m, puede ser null) */
        public final Integer ordinalCardinality;
        
        /** Valor mínimo para variables NUMERIC (obligatorio para NUMERIC) */
        public final Double numericMin;
        
        /** Valor máximo para variables NUMERIC (obligatorio para NUMERIC) */
        public final Double numericMax;
        
        /** Número máximo de selecciones permitidas para NOMINAL_MULTI (puede ser null) */
        public final Integer maxSelections;

        /**
         * Constructor básico con solo el tipo de variable.
         * 
         * @param kind Tipo de variable (no puede ser null)
         */
        public FeatureSpec(VariableKind kind) {
            this(kind, null, null, null, null, null);
        }

        /**
         * Constructor con tipo y orden ordinal.
         * 
         * @param kind Tipo de variable (no puede ser null)
         * @param ordinalOrder Lista ordenada para variables ORDINAL
         */
        public FeatureSpec(VariableKind kind, List<String> ordinalOrder) {
            this(kind, ordinalOrder, null, null, null, null);
        }

        /**
         * Constructor completo con todos los metadatos opcionales.
         * 
         * @param kind Tipo de variable (no puede ser null)
         * @param ordinalOrder Lista ordenada para ORDINAL
         * @param ordinalCardinality Número de modalidades para ORDINAL
         * @param numericMin Valor mínimo para NUMERIC
         * @param numericMax Valor máximo para NUMERIC
         * @param maxSelections Número máximo de selecciones para NOMINAL_MULTI
         */
        public FeatureSpec(VariableKind kind, List<String> ordinalOrder, Integer ordinalCardinality,
                           Double numericMin, Double numericMax, Integer maxSelections) {
            this.kind = Objects.requireNonNull(kind, "kind");
            this.ordinalOrder = ordinalOrder;
            this.ordinalCardinality = ordinalCardinality;
            this.numericMin = numericMin;
            this.numericMax = numericMax;
            this.maxSelections = maxSelections;
        }

        /** Crea una especificación para variable numérica con rango [min, max]. 
         * @param min Valor mínimo del rango (obligatorio)
         * @param max Valor máximo del rango (obligatorio, debe ser > min)
         */
        public static FeatureSpec numeric(Double min, Double max) { return new FeatureSpec(VariableKind.NUMERIC, null, null, min, max, null); }
        
        /** Crea una especificación para variable ordinal con orden de modalidades. 
         * @param order Lista ordenada de modalidades (obligatorio, no puede ser null ni vacía)
         */
        public static FeatureSpec ordinal(List<String> order) { return new FeatureSpec(VariableKind.ORDINAL, order, null, null, null, null); }
        
        /** Crea una especificación para variable ordinal con orden y cardinalidad m. 
         * @param order Lista ordenada de modalidades (obligatorio, no puede ser null ni vacía)
         * @param m Cardinalidad (número de modalidades)
         */
        public static FeatureSpec ordinal(List<String> order, Integer m) { return new FeatureSpec(VariableKind.ORDINAL, order, m, null, null, null); }
        
        /** Crea una especificación para variable nominal simple. */
        public static FeatureSpec nominalSingle() { return new FeatureSpec(VariableKind.NOMINAL_SINGLE); }
        
        /** Crea una especificación para variable nominal múltiple. */
        public static FeatureSpec nominalMulti() { return new FeatureSpec(VariableKind.NOMINAL_MULTI); }
        
        /** Crea una especificación para variable nominal múltiple con máximo de selecciones. */
        public static FeatureSpec nominalMulti(Integer maxSelections) { return new FeatureSpec(VariableKind.NOMINAL_MULTI, null, null, null, null, maxSelections); }
        
        /** Crea una especificación para variable de texto libre. */
        public static FeatureSpec freeText() { return new FeatureSpec(VariableKind.FREE_TEXT); }
    }

    // Nota: Se elimina el método distance(a,b) sin specs para evitar usos ambiguos.
    // A partir de ahora, SIEMPRE se debe proporcionar FeatureSpec[] para cada dimensión.

    /**
     * Calcula la distancia Euclidiana (L2) entre dos vectores heterogéneos.
     * 
     * Fórmula: d(a,b) = √(Σdᵢ²) / N
     * Donde:
     *   - dᵢ = distancia local en la dimensión i (según el tipo de variable)
     *   - N = número de dimensiones (longitud de los vectores)
     * 
     * La normalización por N asegura que la distancia esté acotada y sea comparable
     * independientemente del número de dimensiones.
     * 
     * Uso recomendado: Algoritmos K-Means y K-Means++
     * 
     * @param a Primer vector de valores (como Strings)
     * @param b Segundo vector de valores (como Strings)
     * @param specs Especificaciones de tipo para cada dimensión
     * @return Distancia Euclidiana normalizada entre a y b, en el rango [0, ∞)
     * @throws IllegalArgumentException si algún argumento es null o las longitudes no coinciden
     */
    public double distance(String[] a, String[] b, FeatureSpec[] specs) {
        if (a == null || b == null || specs == null)
            throw new IllegalArgumentException("Arguments cannot be null");
        if (a.length != b.length || a.length != specs.length)
            throw new IllegalArgumentException("Vectors and specs must have same length");

        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            double d = localDistance(a[i], b[i], specs[i]);
            sum += d * d;
        }
        // Normalización solicitada: dividir la raíz por N (número de preguntas/dimensiones)
        double n = (double) a.length;
        double euclidean = Math.sqrt(sum);
        return euclidean / n;
    }

    /**
     * Calcula la distancia Manhattan (L1) entre dos vectores heterogéneos.
     * 
     * Fórmula: d(a,b) = Σ(dᵢ/N)
     * Donde:
     *   - dᵢ = distancia local en la dimensión i (según el tipo de variable)
     *   - N = número de dimensiones (longitud de los vectores)
     * 
     * La normalización se aplica a cada distancia local antes de sumar, lo que
     * asegura que todas las dimensiones contribuyan equitativamente al resultado final.
     * 
     * Ventajas sobre la distancia Euclidiana:
     *   - Más robusta a valores atípicos (outliers)
     *   - Menos sensible a dimensiones con valores extremos
     *   - Comportamiento más estable en espacios de alta dimensionalidad
     * 
     * Uso recomendado: Algoritmo K-Medoids (PAM)
     * 
     * @param a Primer vector de valores (como Strings)
     * @param b Segundo vector de valores (como Strings)
     * @param specs Especificaciones de tipo para cada dimensión
     * @return Distancia Manhattan normalizada entre a y b, en el rango [0, ∞)
     * @throws IllegalArgumentException si algún argumento es null o las longitudes no coinciden
     */
    public double distanceManhattan(String[] a, String[] b, FeatureSpec[] specs) {
        if (a == null || b == null || specs == null)
            throw new IllegalArgumentException("Arguments cannot be null");
        if (a.length != b.length || a.length != specs.length)
            throw new IllegalArgumentException("Vectors and specs must have same length");

        double sum = 0.0;
        double n = (double) a.length;
        for (int i = 0; i < a.length; i++) {
            double d = localDistance(a[i], b[i], specs[i]);
            sum += d / n;  // normalizar cada distancia local antes de sumar
        }
        return sum;
    }

    /**
     * Calcula la distancia local en una dimensión según el tipo de variable.
     * 
     * Delega el cálculo a funciones específicas según el tipo de variable
     * definido en la especificación (FeatureSpec).
     * 
     * @param ai Valor del primer vector en la dimensión i
     * @param bi Valor del segundo vector en la dimensión i
     * @param spec Especificación del tipo de variable para esta dimensión
     * @return Distancia local normalizada en el rango [0, 1]
     */
    private double localDistance(String ai, String bi, FeatureSpec spec) {
        switch (spec.kind) {
            case NUMERIC:
                return distanceNumeric(ai, bi, spec.numericMin, spec.numericMax);
            case ORDINAL:
                return distanceOrdinal(ai, bi, spec.ordinalOrder, spec.ordinalCardinality);
            case NOMINAL_SINGLE:
                return distanceNominalSingle(ai, bi);
            case NOMINAL_MULTI:
                return distanceNominalMulti(ai, bi);
            case FREE_TEXT:
                return distanceFreeText(ai, bi);
            default:
                // No debería ocurrir
                return 0.0;
        }
    }

    // ================== Distancia local por tipo (todos reciben String) ==================

    /**
     * Calcula la distancia entre dos valores numéricos.
     * 
     * Fórmula: d = |a - b| / (max - min)
     * 
     * La normalización por el rango [min, max] asegura que el resultado esté en [0,1],
     * donde 0 indica valores idénticos y 1 indica la máxima diferencia posible en el rango.
     * 
     * Precondición: min y max deben estar definidos y max > min.
     * 
     * @param a Primer valor numérico (como String)
     * @param b Segundo valor numérico (como String)
     * @param min Valor mínimo del rango (no puede ser null)
     * @param max Valor máximo del rango (no puede ser null)
     * @return Distancia normalizada en el rango [0,1]
     * @throws IllegalArgumentException si min o max son null, o si max <= min
     */
    private double distanceNumeric(String a, String b, Double min, Double max) {
        if (a == null || b == null) return 1.0;
        if (min == null || max == null) {
            throw new IllegalArgumentException("min y max son requeridos para variables NUMERIC");
        }
        if (max <= min) {
            throw new IllegalArgumentException("max debe ser mayor que min (max=" + max + ", min=" + min + ")");
        }
        
        try {
            double da = Double.parseDouble(a);
            double db = Double.parseDouble(b);
            double diff = Math.abs(da - db);
            return diff / (max - min);
        } catch (NumberFormatException e) {
            // Si no se pueden parsear como números, considerar máxima discrepancia
            return 1.0;
        }
    }

    /**
     * Calcula la distancia entre dos valores ordinales.
     * 
     * Utiliza las posiciones de los valores en el orden predefinido para calcular
     * la distancia.
     * 
     * Fórmula: d = |pos(a) - pos(b)| / (m-1)
     * Donde m es el número de modalidades.
     * 
     * Ejemplos:
     *   - order = ["bajo", "medio", "alto"], a="bajo", b="alto" → d = 2/2 = 1.0
     *   - order = ["bajo", "medio", "alto"], a="bajo", b="medio" → d = 1/2 = 0.5
     * 
     * Precondición: order debe estar definido y no puede ser vacío.
     * 
     * @param a Primer valor ordinal
     * @param b Segundo valor ordinal
     * @param order Lista ordenada de modalidades (no puede ser null ni vacía)
     * @param m Cardinalidad (número de modalidades, puede ser null)
     * @return Distancia normalizada [0,1], o 1.0 si algún valor no se encuentra en el orden
     * @throws IllegalArgumentException si order es null o vacía
     */
    private double distanceOrdinal(String a, String b, List<String> order, Integer m) {
        if (a == null || b == null) return 1.0;
        if (order == null || order.isEmpty()) {
            throw new IllegalArgumentException("order es requerido para variables ORDINAL");
        }
        
        int ia = order.indexOf(a);
        int ib = order.indexOf(b);
        if (ia < 0 || ib < 0) return 1.0; // Valor no encontrado en el orden
        
        double diff = Math.abs(ia - ib);
        int mm = (m != null) ? m : order.size();
        if (mm >= 2) return diff / (mm - 1.0);
        return diff;
    }

    /**
     * Calcula la distancia entre dos valores nominales simples.
     * 
     * Métrica binaria simple:
     *   - d = 0 si a == b (mismo valor)
     *   - d = 1 si a ≠ b (valores diferentes)
     * 
     * Ejemplo: Si a="rojo" y b="rojo" → d=0; si a="rojo" y b="azul" → d=1
     * 
     * @param a Primer valor nominal
     * @param b Segundo valor nominal
     * @return 0.0 si son iguales, 1.0 si son diferentes
     */
    private double distanceNominalSingle(String a, String b) {
        if (a == null || b == null) return 1.0;
        return a.equals(b) ? 0.0 : 1.0;
    }

    /**
     * Calcula la distancia entre dos conjuntos de valores nominales múltiples.
     * 
     * Utiliza la distancia de Jaccard: d = 1 - J(A,B)
     * Donde J(A,B) es el coeficiente de Jaccard (intersección dividida por unión).
     * 
     * Fórmula: d = 1 - |A ∩ B| / |A ∪ B|
     * 
     * Ejemplos:
     *   - A = {rojo, azul}, B = {azul, verde} → J = 1/3, d = 2/3
     *   - A = {rojo, azul}, B = {rojo, azul} → J = 1, d = 0
     *   - A = {rojo}, B = {verde} → J = 0, d = 1
     * 
     * Los valores deben estar separados por comas: "opcion1,opcion2,opcion3"
     * 
     * @param a Primer conjunto de valores (formato: "valor1,valor2,...")
     * @param b Segundo conjunto de valores (formato: "valor1,valor2,...")
     * @return Distancia de Jaccard en el rango [0,1]
     */
    private double distanceNominalMulti(String a, String b) {
        if (a == null || b == null) return 1.0;
        
        Set<String> sa = parseMultiString(a);
        Set<String> sb = parseMultiString(b);
        
        if (sa.isEmpty() && sb.isEmpty()) return 0.0;
        
        int inter = 0;
        for (String x : sa) if (sb.contains(x)) inter++;
        int uni = sa.size();
        for (String x : sb) if (!sa.contains(x)) uni++;
        
        if (uni == 0) return 0.0;
        double jaccard = inter / (double) uni;
        return 1.0 - jaccard;
    }

    /**
     * Parsea un string con valores separados por comas a un conjunto (Set).
     * 
     * Formato esperado: "opcion1,opcion2,opcion3"
     * Espacios en blanco alrededor de cada valor son eliminados.
     * 
     * @param value String con valores separados por comas
     * @return Set con los valores parseados (vacío si value es null o vacío)
     */
    private Set<String> parseMultiString(String value) {
        Set<String> result = new java.util.HashSet<>();
        if (value == null || value.trim().isEmpty()) return result;
        for (String part : value.split(",")) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) result.add(trimmed);
        }
        return result;
    }

    /**
     * Calcula la distancia entre dos textos libres usando una fórmula basada en Levenshtein.
     * 
     * La distancia de Levenshtein mide el número mínimo de operaciones
     * (inserción, eliminación, sustitución) necesarias para transformar un string en otro.
     * 
     * Fórmula:
     * d = (lev(a,b) - |len(a) - len(b)|) / (max(len(a), len(b)) - |len(a) - len(b)|)
     * 
     * Donde:
     *   - lev(a,b) = distancia de Levenshtein entre a y b
     *   - len(a), len(b) = longitudes de los strings a y b
     *   - |len(a) - len(b)| = diferencia absoluta de longitudes
     * 
     * Esta fórmula penaliza más las diferencias en caracteres cuando los strings
     * tienen longitudes similares, normalizando por la longitud común efectiva.
     * 
     * Casos especiales:
     *   - Si ambos strings están vacíos → d = 0.0
     *   - Si tienen la misma longitud → d = lev / len
     *   - Si el denominador es 0 (uno vacío, otro no) → d = 1.0
     * 
     * @param a Primer texto
     * @param b Segundo texto
     * @return Distancia normalizada en el rango [0,1]
     */
    private double distanceFreeText(String a, String b) {
        if (a == null || b == null) return 1.0;
        
        int lenA = a.length();
        int lenB = b.length();
        
        // Caso especial: ambos vacíos
        if (lenA == 0 && lenB == 0) return 0.0;
        
        int lev = levenshtein(a, b);
        int maxLen = Math.max(lenA, lenB);
        int diffLen = Math.abs(lenA - lenB);
        
        // Numerador: lev - |len(a) - len(b)|
        int numerator = lev - diffLen;
        
        // Denominador: max(len(a), len(b)) - |len(a) - len(b)|
        int denominator = maxLen - diffLen;
        
        // Si el denominador es 0, significa que uno es vacío y el otro no
        // o que la diferencia de longitudes es igual a la longitud máxima
        if (denominator == 0) return 1.0;
        
        return numerator / (double) denominator;
    }

    /**
     * Calcula la distancia de Levenshtein entre dos strings usando programación dinámica.
     * 
     * Representa el número mínimo de operaciones de edición (inserción, eliminación,
     * sustitución) necesarias para transformar el string s en el string t.
     * 
     * Algoritmo: Programación dinámica con optimización de espacio O(n)
     * en lugar de O(m×n), usando solo dos arrays en lugar de una matriz completa.
     * 
     * Complejidad:
     *   - Tiempo: O(m × n), donde m = |s|, n = |t|
     *   - Espacio: O(n) - solo almacena dos filas en lugar de la matriz completa
     * 
     * @param s String origen
     * @param t String destino
     * @return Número mínimo de operaciones de edición necesarias
     */
    private int levenshtein(String s, String t) {
        int m = s.length();
        int n = t.length();
        
        // Caso base: si alguno está vacío, la distancia es la longitud del otro
        if (m == 0) return n;
        if (n == 0) return m;
        
        // Matriz de programación dinámica (solo necesitamos dos filas)
        int[] prev = new int[n + 1];
        int[] curr = new int[n + 1];
        
        // Inicializar primera fila
        for (int j = 0; j <= n; j++) prev[j] = j;
        
        // Calcular distancia
        for (int i = 1; i <= m; i++) {
            curr[0] = i;
            for (int j = 1; j <= n; j++) {
                int cost = (s.charAt(i - 1) == t.charAt(j - 1)) ? 0 : 1;
                curr[j] = Math.min(
                    Math.min(prev[j] + 1,      // eliminación
                             curr[j - 1] + 1), // inserción
                    prev[j - 1] + cost         // sustitución
                );
            }
            // Intercambiar filas
            int[] temp = prev;
            prev = curr;
            curr = temp;
        }
        
        return prev[n];
    }
}
