package edu.upc.prop.clusterxx.domini.classes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Representa un clúster con centroide heterogéneo (String[]) y sus miembros.
 */
public class Kluster {
    private String[] centroid;
    private final List<String[]> members;

    public Kluster(String[] centroid) {
        if (centroid == null || centroid.length == 0) throw new IllegalArgumentException("Centroid cannot be null or empty");
        this.centroid = Arrays.copyOf(centroid, centroid.length);
        this.members = new ArrayList<>();
    }

    public String[] getCentroid() { return Arrays.copyOf(centroid, centroid.length); }

    public void setCentroid(String[] c) {
        if (c == null || c.length != centroid.length)
            throw new IllegalArgumentException("Centroid must be non-null and same dimension");
        this.centroid = Arrays.copyOf(c, c.length);
    }

    public List<String[]> getMembers() { return new ArrayList<>(members); }

    public void addMember(String[] v) { members.add(Arrays.copyOf(v, v.length)); }

    public void clearMembers() { members.clear(); }

    public int size() { return members.size(); }


    //Revisar recompute dels centroides, fer igual que pdf (part centroide dun cluster)
    /**
     * Recalcula el centroide según el tipo de cada dimensión definido en specs.
     * - NUMERIC: media aritmética (como String)
     * - ORDINAL, NOMINAL_SINGLE, NOMINAL_MULTI, FREE_TEXT: moda (valor más frecuente)
     * Devuelve true si hubo cambios.
     */
    public boolean recomputeCentroid(DistanceCalculator.FeatureSpec[] specs) {
        if (members.isEmpty()) return false;
        if (specs == null || specs.length != centroid.length) 
            throw new IllegalArgumentException("specs must match centroid dimension");
        
        int dim = centroid.length;
        String[] newC = new String[dim];

        for (int i = 0; i < dim; i++) {
            if (specs[i].kind == DistanceCalculator.VariableKind.NUMERIC) {
                // Media aritmética
                newC[i] = computeNumericMean(i);
            } else {
                // Moda (para ORDINAL, NOMINAL_SINGLE, NOMINAL_MULTI, FREE_TEXT)
                newC[i] = computeMode(i);
            }
        }

        boolean changed = !Arrays.equals(centroid, newC);
        this.centroid = newC;
        return changed;
    }

    /** Calcula la media de valores numéricos en la dimensión i, devuelve como String. */
    private String computeNumericMean(int i) {
        double sum = 0.0;
        int count = 0;
        for (String[] m : members) {
            try {
                sum += Double.parseDouble(m[i]);
                count++;
            } catch (NumberFormatException e) {
                // Ignorar valores no numéricos
            }
        }
        if (count == 0) return centroid[i]; // Mantener actual si no hay valores válidos
        double mean = sum / count;
        // Redondear a entero si todos los valores originales eran enteros
        if (allIntegersInDimension(i)) {
            return String.valueOf((int) Math.round(mean));
        }
        return String.valueOf(mean);
    }

    /** Verifica si todos los valores en la dimensión i son enteros (sin decimales). */
    private boolean allIntegersInDimension(int i) {
        for (String[] m : members) {
            try {
                double val = Double.parseDouble(m[i]);
                if (val != Math.floor(val)) return false;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }

    /** Calcula la moda (valor más frecuente) en la dimensión i. En empate, mantiene el actual. */
    private String computeMode(int i) {
        Map<String, Integer> freq = new HashMap<>();
        for (String[] m : members) {
            String val = m[i];
            freq.put(val, freq.getOrDefault(val, 0) + 1);
        }
        
        String best = centroid[i];
        int bestCnt = freq.getOrDefault(best, 0);
        
        for (Map.Entry<String, Integer> e : freq.entrySet()) {
            if (e.getValue() > bestCnt) {
                bestCnt = e.getValue();
                best = e.getKey();
            }
        }
        return best;
    }

    @Override
    public String toString() { return "Kluster{centroid=" + Arrays.toString(centroid) + ", size=" + members.size() + "}"; }
}
