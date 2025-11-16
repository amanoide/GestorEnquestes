package edu.upc.prop.clusterxx.domini.classes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Representa un clúster de datos heterogéneos con centroide y miembros.
 * 
 * <p>Un clúster agrupa vectores de datos similares alrededor de un punto central
 * (centroide). Esta implementación soporta datos heterogéneos donde cada dimensión
 * puede ser de diferente tipo (numérica, ordinal, nominal, texto libre).</p>
 * 
 * <p><b>Características principales:</b></p>
 * <ul>
 *   <li>Centroide representado como String[] para soportar tipos heterogéneos</li>
 *   <li>Lista de miembros (puntos asignados al cluster)</li>
 *   <li>Recálculo de centroide según tipo de variable:
 *     <ul>
 *       <li>NUMERIC: Media aritmética (X̄ⱼᵖ = 1/Nₚ Σ Xᵢⱼᵖ)</li>
 *       <li>ORDINAL, NOMINAL_SINGLE: Moda (moda(Xⱼᵖ))</li>
 *       <li>NOMINAL_MULTI: Argmax frecuencia (argmax freq(Xᵢⱼᵖ))</li>
 *       <li>FREE_TEXT: Palabra semántica más frecuente (argmax freq(semWᵢⱼᵖ(k)))</li>
 *     </ul>
 *   </li>
 * </ul>
 * 
 * <p><b>Uso en algoritmos de clustering:</b></p>
 * <ul>
 *   <li>K-Means: Recalcula centroides como media/moda tras cada iteración</li>
 *   <li>K-Means++: Inicializa centroides con puntos reales</li>
 *   <li>K-Medoids: El centroide es siempre un punto real (medoide)</li>
 * </ul>
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
     * <p>El centroide inicial suele ser un punto del dataset (K-Means++, K-Medoids)
     * o generado aleatoriamente (K-Means básico).</p>
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
     * <p>Devuelve una copia defensiva para evitar modificaciones externas.</p>
     * 
     * @return Copia del vector centroide
     */
    public String[] getCentroid() { return Arrays.copyOf(centroid, centroid.length); }

    /**
     * Establece un nuevo centroide para el cluster.
     * 
     * <p>El nuevo centroide debe tener la misma dimensionalidad que el actual.</p>
     * 
     * @param c Nuevo vector centroide (no puede ser null, debe tener misma longitud)
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
     * <p>Devuelve una copia defensiva para evitar modificaciones externas.</p>
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
     * <p>Se almacena una copia del vector para evitar modificaciones externas.</p>
     * 
     * @param v Vector a añadir como miembro del cluster
     */
    public void addMember(String[] v) { members.add(Arrays.copyOf(v, v.length)); }

    /**
     * Elimina todos los miembros del cluster.
     * 
     * <p>Útil al inicio de cada iteración en algoritmos iterativos de clustering.</p>
     */
    public void clearMembers() { members.clear(); }

    /**
     * Devuelve el número de miembros en el cluster.
     * 
     * @return Cantidad de puntos asignados a este cluster
     */
    public int size() { return members.size(); }


    /**
     * Recalcula el centroide del cluster según los tipos de variables.
     * 
     * <p>Aplica diferentes estrategias de agregación según el tipo de cada dimensión:</p>
     * 
     * <ul>
     *   <li><b>NUMERIC:</b> Media aritmética de todos los valores
     *     <ul>
     *       <li>Fórmula: X̄ⱼᵖ = (1/Nₚ) Σ Xᵢⱼᵖ</li>
     *       <li>Se mantiene precisión decimal completa</li>
     *       <li>Ignora valores que no se pueden parsear</li>
     *     </ul>
     *   </li>
     *   <li><b>ORDINAL:</b> Moda (valor más frecuente)
     *     <ul>
     *       <li>Fórmula: moda(Xⱼᵖ)</li>
     *       <li>En caso de empate, mantiene el centroide actual</li>
     *     </ul>
     *   </li>
     *   <li><b>NOMINAL_SINGLE:</b> Moda (valor más frecuente)
     *     <ul>
     *       <li>Fórmula: moda(Xⱼᵖ)</li>
     *       <li>Equivalente a argmax freq(Xᵢⱼᵖ)</li>
     *     </ul>
     *   </li>
     *   <li><b>NOMINAL_MULTI:</b> Valor con máxima frecuencia
     *     <ul>
     *       <li>Fórmula: argmax freq(Xᵢⱼᵖ)</li>
     *       <li>Cuenta frecuencia de cada combinación de valores</li>
     *     </ul>
     *   </li>
     *   <li><b>FREE_TEXT:</b> Palabra semántica más frecuente
     *     <ul>
     *       <li>Fórmula: argmax freq(semWᵢⱼᵖ(k))</li>
     *       <li>Extrae palabras de todos los textos del cluster</li>
     *       <li>Calcula frecuencia de cada palabra</li>
     *       <li>El centroide es la palabra (no el texto completo) más frecuente</li>
     *     </ul>
     *   </li>
     * </ul>
     * 
     * <p><b>Uso en algoritmos:</b></p>
     * <ul>
     *   <li>K-Means: Llamado al final de cada iteración tras reasignar puntos</li>
     *   <li>Detecta convergencia cuando el centroide no cambia</li>
     * </ul>
     * 
     * <p><b>Nota:</b> K-Medoids NO usa este método, ya que el centroide siempre
     * debe ser un punto real del dataset (medoide), no un punto calculado.</p>
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
     * <p>Parsea los valores de todos los miembros en la dimensión i como números
     * y calcula su promedio. La media se devuelve con precisión decimal completa
     * para asegurar la exactitud del algoritmo K-Means.</p>
     * 
     * <p><b>Manejo de errores:</b></p>
     * <ul>
     *   <li>Ignora valores que no se pueden parsear como números</li>
     *   <li>Si ningún valor es válido (count=0), mantiene el centroide actual</li>
     * </ul>
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
     * <p>Cuenta las frecuencias de todos los valores en la dimensión i y selecciona
     * el que aparece más veces. Esta estrategia se usa para variables cualitativas
     * (ordinales y nominales simples) donde la media no tiene sentido.</p>
     * 
     * <p><b>Fórmulas según tipo:</b></p>
     * <ul>
     *   <li>ORDINAL, NOMINAL_SINGLE: moda(Xⱼᵖ)</li>
     *   <li>NOMINAL_MULTI: argmax freq(Xᵢⱼᵖ)</li>
     * </ul>
     * 
     * <p>Nota: Aunque el PDF usa diferentes notaciones (moda vs argmax), son
     * matemáticamente equivalentes: la moda es el valor que maximiza la frecuencia.</p>
     * 
     * <p><b>Desempate:</b> En caso de empate entre varios valores con la misma frecuencia
     * máxima, mantiene el centroide actual si está entre los empatados. Esto
     * proporciona estabilidad y evita cambios innecesarios.</p>
     * 
     * @param i Índice de la dimensión a calcular
     * @return Valor más frecuente (moda / argmax freq) en la dimensión i
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
     * Calcula la palabra semántica más frecuente para texto libre.
     * 
     * <p>Este método implementa la fórmula del PDF para variables FREE_TEXT:</p>
     * <pre>
     * X̄ⱼᵖ = argmax freq(semWᵢⱼᵖ(k))
     * </pre>
     * 
     * <p><b>Procedimiento:</b></p>
     * <ol>
     *   <li>Extrae todas las palabras semánticas de los textos en la dimensión i</li>
     *   <li>Calcula la frecuencia de cada palabra a través de todos los textos</li>
     *   <li>Devuelve la palabra con mayor frecuencia</li>
     * </ol>
     * 
     * <p><b>Tokenización:</b> Las palabras se extraen separando por espacios y
     * eliminando puntuación. Se convierten a minúsculas para normalizar.</p>
     * 
     * <p><b>Desempate:</b> En caso de empate, si el centroide actual es una de las
     * palabras más frecuentes, se mantiene para proporcionar estabilidad.</p>
     * 
     * <p><b>Casos especiales:</b></p>
     * <ul>
     *   <li>Si no se encuentran palabras válidas, mantiene el centroide actual</li>
     *   <li>Palabras vacías o solo con espacios se ignoran</li>
     * </ul>
     * 
     * @param i Índice de la dimensión a calcular
     * @return Palabra semántica más frecuente en la dimensión i
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
