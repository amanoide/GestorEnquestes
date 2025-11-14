package edu.upc.prop.clusterxx.domini.classes;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/** Calcula distancias entre vectores heterogéneos. */
public class DistanceCalculator {

    /** Tipos de variables soportadas para la distancia local. */
    public enum VariableKind {
        NUMERIC,            // cuantitativa: un solo número
        ORDINAL,            // cualitativa ordenada: un valor con orden predefinido
        NOMINAL_SINGLE,     // cualitativa no ordenada (un solo valor)
        NOMINAL_MULTI,      // cualitativa no ordenada múltiple (conjunto de valores)
        FREE_TEXT           // string libre
    }

    /**
     * Especificación por dimensión con metadatos necesarios para calcular la distancia local.
     * - kind: tipo de variable
     * - ordinalOrder: lista ordenada de modalidades para variables ordinales (opcional)
     * - domain: conjunto de modalidades posibles (para nominal simple o múltiple) (opcional)
     */
    public static class FeatureSpec {
        public final VariableKind kind;
        public final List<String> ordinalOrder; // sólo para ORDINAL (puede ser null hasta definir fórmula)
        public final Integer ordinalCardinality; // m = número de modalidades (para ORDINAL, puede ser null)
    public final Set<String> domain;        // para NOMINAL_* (opcional; actualmente NO se usa en el cálculo)
        // Metadatos opcionales útiles
        public final Double numericMin;         // para NUMERIC (puede ser null)
        public final Double numericMax;         // para NUMERIC (puede ser null)
        public final Integer maxSelections;     // para NOMINAL_MULTI (puede ser null)

        public FeatureSpec(VariableKind kind) {
            this(kind, null, null);
        }

        public FeatureSpec(VariableKind kind, List<String> ordinalOrder, Set<String> domain) {
            this(kind, ordinalOrder, null, domain, null, null, null);
        }

        public FeatureSpec(VariableKind kind, List<String> ordinalOrder, Integer ordinalCardinality, Set<String> domain,
                           Double numericMin, Double numericMax, Integer maxSelections) {
            this.kind = Objects.requireNonNull(kind, "kind");
            this.ordinalOrder = ordinalOrder;
            this.ordinalCardinality = ordinalCardinality;
            this.domain = domain;
            this.numericMin = numericMin;
            this.numericMax = numericMax;
            this.maxSelections = maxSelections;
        }

        public static FeatureSpec numeric() { return new FeatureSpec(VariableKind.NUMERIC); }
        public static FeatureSpec numeric(Double min, Double max) { return new FeatureSpec(VariableKind.NUMERIC, null, null, null, min, max, null); }
        public static FeatureSpec ordinal(List<String> order) { return new FeatureSpec(VariableKind.ORDINAL, order, null, null, null, null, null); }
        public static FeatureSpec ordinal(List<String> order, Integer m) { return new FeatureSpec(VariableKind.ORDINAL, order, m, null, null, null, null); }
        public static FeatureSpec ordinalWithM(Integer m) { return new FeatureSpec(VariableKind.ORDINAL, null, m, null, null, null, null); }
        public static FeatureSpec nominalSingle(Set<String> domain) { return new FeatureSpec(VariableKind.NOMINAL_SINGLE, null, null, domain, null, null, null); }
        public static FeatureSpec nominalMulti(Set<String> domain) { return new FeatureSpec(VariableKind.NOMINAL_MULTI, null, null, domain, null, null, null); }
        public static FeatureSpec nominalMulti(Set<String> domain, Integer maxSelections) { return new FeatureSpec(VariableKind.NOMINAL_MULTI, null, null, domain, null, null, maxSelections); }
        public static FeatureSpec freeText() { return new FeatureSpec(VariableKind.FREE_TEXT); }
    }

    // Nota: Se elimina el método distance(a,b) sin specs para evitar usos ambiguos.
    // A partir de ahora, SIEMPRE se debe proporcionar FeatureSpec[] para cada dimensión.

    /**
     * Distancia Euclídea generalizada: sqrt( sum(d_i^2) ) seleccionando la distancia local por tipo de variable.
     * Todos los valores vienen como String, y se convierten según el tipo de variable (FeatureSpec.kind).
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

    /** Calcula la distancia local en una dimensión según el tipo de variable. */
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
     * NUMERIC: convierte el String a double y calcula |a - b| / (max - min).
     * Si no se proporcionan min/max, devuelve |a - b| sin normalizar.
     */
    private double distanceNumeric(String a, String b, Double min, Double max) {
        if (a == null || b == null) return 1.0;
        try {
            double da = Double.parseDouble(a);
            double db = Double.parseDouble(b);
            double diff = Math.abs(da - db);
            
            // Normalizar por el rango si está disponible
            if (min != null && max != null && max > min) {
                return diff / (max - min);
            }
            
            // Sin normalización, devolver la diferencia absoluta
            return diff;
        } catch (NumberFormatException e) {
            // Si no se pueden parsear como números, considerar máxima discrepancia
            return 1.0;
        }
    }

    /**
     * ORDINAL: usa el orden de modalidades para calcular |pos(a) - pos(b)| / (m-1).
     */
    private double distanceOrdinal(String a, String b, List<String> order, Integer m) {
        if (a == null || b == null) return 1.0;
        if (order == null || order.isEmpty()) {
            // Sin orden definido, usar igualdad básica
            return a.equals(b) ? 0.0 : 1.0;
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
     * NOMINAL_SINGLE: 0 si son iguales, 1 si distintos.
     */
    private double distanceNominalSingle(String a, String b) {
        if (a == null || b == null) return 1.0;
        return a.equals(b) ? 0.0 : 1.0;
    }

    /**
     * NOMINAL_MULTI: parsea los strings como "opcion1,opcion2,opcion3" y calcula 1 - Jaccard.
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

    /** Parsea un string "opcion1,opcion2,opcion3" a Set<String>. */
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
     * FREE_TEXT: distancia de Levenshtein normalizada entre strings.
     */
    private double distanceFreeText(String a, String b) {
        if (a == null || b == null) return 1.0;
        
        int lev = levenshtein(a, b);
        int maxLen = Math.max(a.length(), b.length());
        
        // Normalizar por la longitud máxima
        if (maxLen == 0) return 0.0;
        return lev / (double) maxLen;
    }

    /**
     * Calcula la distancia de Levenshtein entre dos strings usando programación dinámica.
     * Representa el número mínimo de operaciones (inserción, eliminación, sustitución) 
     * para transformar s en t.
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
