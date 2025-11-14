package edu.upc.prop.clusterxx.domini.classes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Selección de centroides iniciales con KMeans++ usando FeatureSpec. */
public class KMeansPlusPlus {
    private final DistanceCalculator dc = new DistanceCalculator();
    private final Random rnd;

    public KMeansPlusPlus() { this(new Random()); }
    public KMeansPlusPlus(Random rnd) { this.rnd = (rnd == null ? new Random() : rnd); }

    /** Devuelve clusters tras ejecutar KMeans con inicialización KMeans++. */
    public List<Kluster> fit(List<String[]> data, int k, int maxIters, DistanceCalculator.FeatureSpec[] specs) {
        List<String[]> init = initCentroids(data, k, specs);
        return new KMeans(rnd).fitWithInitialCentroids(data, init, maxIters, specs);
    }

    /** KMeans++: elige el primero al azar; cada siguiente según prob. proporcional a D(x)^2. */
    public List<String[]> initCentroids(List<String[]> data, int k, DistanceCalculator.FeatureSpec[] specs) {
        if (data == null || data.isEmpty()) throw new IllegalArgumentException("data empty");
        if (k <= 0 || k > data.size()) throw new IllegalArgumentException("invalid k");
        if (specs == null) throw new IllegalArgumentException("specs required");

        List<String[]> centroids = new ArrayList<>();
        centroids.add(data.get(rnd.nextInt(data.size())));
        while (centroids.size() < k) {
            // calcular D(x)^2 para cada punto x (distancia al centroide más cercano)
            double[] d2 = new double[data.size()];
            double sum = 0.0;
            for (int i = 0; i < data.size(); i++) {
                double best = Double.POSITIVE_INFINITY;
                for (String[] c : centroids) {
                    best = Math.min(best, dc.distance(data.get(i), c, specs));
                }
                d2[i] = best * best;
                sum += d2[i];
            }
            // ruleta
            double r = rnd.nextDouble() * sum;
            int chosen = 0;
            for (; chosen < d2.length; chosen++) {
                r -= d2[chosen];
                if (r <= 0) break;
            }
            centroids.add(data.get(Math.min(chosen, data.size() - 1)));
        }
        return centroids;
    }
}
