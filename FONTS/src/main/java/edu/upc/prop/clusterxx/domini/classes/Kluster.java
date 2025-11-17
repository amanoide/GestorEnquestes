package edu.upc.prop.clusterxx.domini.classes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Representa un clúster de datos heterogéneos con centroide y miembros.
 * 
 * Un clúster agrupa vectores de datos similares alrededor de un centroide.
 * Soporta datos heterogéneos donde cada dimensión puede ser de diferente tipo.
 * 
 * Características principales:
 *   - Centroide representado como String[] para soportar tipos heterogéneos
 *   - Lista de miembros (puntos asignados al cluster)
 *   - Recálculo de centroide según tipo de variable:
 *       - NUMERIC: Media aritmética (X̄ⱼᵖ = 1/Nₚ Σ Xᵢⱼᵖ)
 *       - ORDINAL, NOMINAL_SINGLE: Moda (moda(Xⱼᵖ))
 *       - NOMINAL_MULTI: Argmax frecuencia (argmax freq(Xᵢⱼᵖ))
 *       - FREE_TEXT: Palabra semántica más frecuente (argmax freq(semWᵢⱼᵖ(k)))
 * 
 * Uso en algoritmos de clustering:
 *   - K-Means: Recalcula centroides como media/moda tras cada iteración
 *   - K-Means++: Inicializa centroides con puntos reales
 *   - K-Medoids: El centroide es siempre un punto real (medoide)
 * 
 * @author ClusterXX Team
 * @version 2.0
 */
public class Kluster {
    /** Centroide del cluster (punto central representativo) */
    private String[] centroid;
    
    /** Lista de miembros (puntos asignados a este cluster) */
    private final List<String[]> members;

    /**
     * Construye un nuevo cluster con el centroide especificado.
     * 
     * El centroide inicial suele ser un punto del dataset (K-Means++, K-Medoids)
     * o generado aleatoriamente (K-Means básico).
     * 
     * @param centroid Vector inicial del centroide (no puede ser null ni vacío)
     * @throws IllegalArgumentException si centroid es null o vacío
     */
    public Kluster(String[] centroid) {
        if (centroid == null || centroid.length == 0) throw new IllegalArgumentException("Centroid cannot be null or empty");
        this.centroid = Arrays.copyOf(centroid, centroid.length);
        this.members = new ArrayList<>();
    }

    /**
     * Obtiene una copia del centroide del cluster.
     * 
     * @return Copia del vector centroide
     */
    public String[] getCentroid() { return Arrays.copyOf(centroid, centroid.length); }

    /**
     * Establece un nuevo centroide para el cluster.
     * 
     * @param c Nuevo vector centroide (debe tener misma longitud que el actual)
     * @throws IllegalArgumentException si c es null o tiene dimensión diferente
     */
    public void setCentroid(String[] c) {
        if (c == null || c.length != centroid.length)
            throw new IllegalArgumentException("Centroid must be non-null and same dimension");
        this.centroid = Arrays.copyOf(c, c.length);
    }

    /**
     * Obtiene una copia de la lista de miembros del cluster.
     * 
     * @return Nueva lista con copias de los vectores miembro
     */
    public List<String[]> getMembers() {
        List<String[]> copy = new ArrayList<>(members.size());
        for (String[] member : members) {
            copy.add(Arrays.copyOf(member, member.length));
        }
        return copy;
    }

    /**
     * Añade un nuevo miembro al cluster.
     * 
     * @param v Vector a añadir como miembro del cluster
     */
    public void addMember(String[] v) { members.add(Arrays.copyOf(v, v.length)); }

    /**
     * Elimina todos los miembros del cluster.
     */
    public void clearMembers() { members.clear(); }

    /**
     * Devuelve el número de miembros en el cluster.
     * 
     * @return Cantidad de puntos asignados a este cluster
     */
    public int size() { return members.size(); }

    /**
     * Obtiene el representante real del cluster (punto más cercano al centroide).
     * 
     * En K-Means/K-Means++, el centroide es calculado (media/moda) y puede no existir
     * en el dataset. Este método devuelve el miembro real más cercano.
     * 
     * En K-Medoids, el centroide ya es un punto real (medoide).
     * 
     * @param specs Especificaciones de tipo para cada dimensión
     * @param dc DistanceCalculator para calcular distancias
     * @return Copia del punto real más cercano al centroide (representante del cluster)
     * @throws IllegalArgumentException si specs es null o dc es null
     */
    public String[] getRepresentant(DistanceCalculator.FeatureSpec[] specs, DistanceCalculator dc) {
        if (specs == null || dc == null) {
            throw new IllegalArgumentException("specs and dc cannot be null");
        }
        
        // Si no hay miembros, devolver el centroide
        if (members.isEmpty()) {
            return Arrays.copyOf(centroid, centroid.length);
        }
        
        // Comprobar si el centroide es exactamente uno de los miembros
        // (caso típico de K-Medoids)
        for (String[] member : members) {
            if (Arrays.equals(member, centroid)) {
                return Arrays.copyOf(member, member.length);
            }
        }
        
        // Encontrar el miembro más cercano al centroide
        String[] closest = members.get(0);
        double minDist = dc.distance(centroid, closest, specs);
        
        for (int i = 1; i < members.size(); i++) {
            String[] member = members.get(i);
            double dist = dc.distance(centroid, member, specs);
            if (dist < minDist) {
                minDist = dist;
                closest = member;
            }
        }
        
        return Arrays.copyOf(closest, closest.length);
    }


    /**
     * Recalcula el centroide del cluster según los tipos de variables.
     * 
     * Aplica diferentes estrategias de agregación según el tipo de cada dimensión:
     * 
     * NUMERIC: Media aritmética de todos los valores
     *   - Fórmula: X̄ⱼᵖ = (1/Nₚ) Σ Xᵢⱼᵖ
     *   - Se mantiene precisión decimal completa
     *   - Ignora valores que no se pueden parsear
     * 
     * ORDINAL: Moda (valor más frecuente)
     *   - Fórmula: moda(Xⱼᵖ)
     *   - En caso de empate, mantiene el centroide actual
     * 
     * NOMINAL_SINGLE: Moda (valor más frecuente)
     *   - Fórmula: moda(Xⱼᵖ)
     *   - Equivalente a argmax freq(Xᵢⱼᵖ)
     * 
     * NOMINAL_MULTI: Valor con máxima frecuencia
     *   - Fórmula: argmax freq(Xᵢⱼᵖ)
     *   - Cuenta frecuencia de cada combinación de valores
     * 
     * FREE_TEXT: Palabra semántica más frecuente
     *   - Fórmula: argmax freq(semWᵢⱼᵖ(k))
     *   - Extrae palabras de todos los textos del cluster
     *   - Calcula frecuencia de cada palabra
     *   - El centroide es la palabra (no el texto completo) más frecuente
     * 
     * Uso en algoritmos:
     *   - K-Means: Llamado al final de cada iteración tras reasignar puntos
     *   - Detecta convergencia cuando el centroide no cambia
     * 
     * Nota: K-Medoids NO usa este método, ya que el centroide siempre
     * debe ser un punto real del dataset (medoide), no un punto calculado.
     * 
     * @param specs Especificaciones de tipo para cada dimensión (debe coincidir con dimensión del centroide)
     * @return true si el centroide cambió, false si permaneció igual (indica convergencia)
     * @throws IllegalArgumentException si specs es null o tiene longitud diferente al centroide
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
            } else if (specs[i].kind == DistanceCalculator.VariableKind.FREE_TEXT) {
                // Palabra semántica más frecuente
                newC[i] = computeMostFrequentWord(i);
            } else {
                // Moda para ORDINAL, NOMINAL_SINGLE, NOMINAL_MULTI
                newC[i] = computeMode(i);
            }
        }

        boolean changed = !Arrays.equals(centroid, newC);
        this.centroid = newC;
        return changed;
    }

    /**
     * Calcula la media aritmética de valores numéricos en una dimensión específica.
     * 
     * Ignora valores que no se pueden parsear. Si ningún valor es válido, mantiene el centroide actual.
     * 
     * @param i Índice de la dimensión a calcular
     * @return Media aritmética como String con precisión decimal
     */
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
        return String.valueOf(mean);
    }

    /**
     * Calcula la moda (valor más frecuente) en una dimensión específica.
     * 
     * Usado para variables cualitativas donde la media no tiene sentido.
     * En caso de empate, mantiene el centroide actual.
     * 
     * @param i Índice de la dimensión a calcular
     * @return Valor más frecuente en la dimensión i
     */
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

    /**
     * Calcula la palabra más frecuente para texto libre.
     * 
     * Este método implementa la fórmula del PDF para variables FREE_TEXT:
     * X̄ⱼᵖ = argmax freq(semWᵢⱼᵖ(k))
     * 
     * Procedimiento:
     *   1. Extrae todas las palabras semánticas de los textos en la dimensión i
     *   2. Calcula la frecuencia de cada palabra a través de todos los textos
     *   3. Devuelve la palabra con mayor frecuencia
     * 
     * Tokenización: Las palabras se extraen separando por espacios y
     * eliminando puntuación. Se convierten a minúsculas para normalizar.
     * 
     * Desempate: En caso de empate, si el centroide actual es una de las
     * palabras más frecuentes, se mantiene para proporcionar estabilidad.
     * 
     * Casos especiales:
     *   - Si no se encuentran palabras válidas, mantiene el centroide actual
     *   - Palabras vacías o solo con espacios se ignoran
     * 
     * @param i Índice de la dimensión a calcular
     * @return Palabra más frecuente en la dimensión i
     */
    private String computeMostFrequentWord(int i) {
        Map<String, Integer> wordFreq = new HashMap<>();
        
        // Extraer y contar todas las palabras de todos los textos
        for (String[] m : members) {
            String text = m[i];
            if (text == null || text.trim().isEmpty()) continue;
            
            // Tokenizar: separar por espacios y eliminar puntuación
            String[] words = text.toLowerCase()
                                .replaceAll("[^a-záàéèíïóòúüçñA-ZÁÀÉÈÍÏÓÒÚÜÇÑ0-9\\s]", " ")
                                .trim()
                                .split("\\s+");
            
            for (String word : words) {
                if (word.isEmpty()) continue;
                wordFreq.put(word, wordFreq.getOrDefault(word, 0) + 1);
            }
        }
        
        // Si no hay palabras, mantener centroide actual
        if (wordFreq.isEmpty()) return centroid[i];
        
        // Encontrar la palabra más frecuente
        String bestWord = centroid[i];
        int bestCount = wordFreq.getOrDefault(bestWord.toLowerCase(), 0);
        
        for (Map.Entry<String, Integer> e : wordFreq.entrySet()) {
            if (e.getValue() > bestCount) {
                bestCount = e.getValue();
                bestWord = e.getKey();
            }
        }
        
        return bestWord;
    }

    /**
     * Devuelve una representación en texto del cluster.
     * 
     * @return String con el centroide y el tamaño del cluster
     */
    @Override
    public String toString() { return "Kluster{centroid=" + Arrays.toString(centroid) + ", size=" + members.size() + "}"; }
}
