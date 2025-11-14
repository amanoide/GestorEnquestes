package edu.upc.prop.clusterxx.domini.classes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/** KMeans sobre vectores de String usando FeatureSpec por dimensión. */
public class KMeans {
    private final DistanceCalculator dc = new DistanceCalculator();
    private final Random rnd;

    public KMeans() { this(new Random()); }
    public KMeans(Random rnd) { this.rnd = (rnd == null ? new Random() : rnd); }

    /** Entrena KMeans con inicialización aleatoria. Requiere specs por dimensión. */
    public List<Kluster> fit(List<String[]> data, int k, int maxIters, DistanceCalculator.FeatureSpec[] specs) {
        if (data == null || data.isEmpty()) throw new IllegalArgumentException("data empty");
        if (k <= 0 || k > data.size()) throw new IllegalArgumentException("invalid k");
        if (maxIters <= 0) maxIters = 100;
        if (specs == null) throw new IllegalArgumentException("specs required");

        // Inicialización: escoger k puntos distintos
        List<String[]> init = pickDistinct(data, k);
        return fitWithInitialCentroids(data, init, maxIters, specs);
    }

    /** Entrena KMeans con centroides iniciales dados. Requiere specs por dimensión. */
    public List<Kluster> fitWithInitialCentroids(List<String[]> data, List<String[]> initialCentroids, int maxIters, DistanceCalculator.FeatureSpec[] specs) {
        if (initialCentroids == null || initialCentroids.size() == 0) throw new IllegalArgumentException("no initial centroids");
        if (specs == null) throw new IllegalArgumentException("specs required");
        int k = initialCentroids.size();
        List<Kluster> clusters = new ArrayList<>(k);
        for (String[] c : initialCentroids) clusters.add(new Kluster(c));

        for (int iter = 0; iter < maxIters; iter++) {
            // limpiar miembros
            for (Kluster cl : clusters) cl.clearMembers();
            // asignar
            for (String[] x : data) {
                int best = 0; double bestD = Double.POSITIVE_INFINITY;
                for (int j = 0; j < k; j++) {
                    double d = dc.distance(x, clusters.get(j).getCentroid(), specs);
                    if (d < bestD) { bestD = d; best = j; }
                }
                clusters.get(best).addMember(x);
            }
            // evitar clusters vacíos: resembrar con un punto aleatorio
            for (int j = 0; j < k; j++) {
                if (clusters.get(j).size() == 0) {
                    String[] seed = data.get(rnd.nextInt(data.size()));
                    clusters.set(j, new Kluster(seed));
                }
            }
            // recomputar centroides y comprobar cambios
            boolean changed = false;
            for (Kluster cl : clusters) {
                changed |= cl.recomputeCentroid(specs);
            }
            if (!changed) break;
        }
        return clusters;
    }

    private List<String[]> pickDistinct(List<String[]> data, int k) {
        Set<Integer> idx = new HashSet<>();
        while (idx.size() < k) idx.add(rnd.nextInt(data.size()));
        List<String[]> res = new ArrayList<>(k);
        for (int i : idx) res.add(data.get(i));
        return res;
    }
}
