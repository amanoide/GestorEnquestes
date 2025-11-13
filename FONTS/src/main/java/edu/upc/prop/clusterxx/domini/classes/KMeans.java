package edu.upc.prop.clusterxx.domini.classes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Implementación simple de KMeans para vectores double[].
 */
public class KMeans implements ClusteringAlgorithm {
    public enum Distance { EUCLIDEAN, MANHATTAN }

    private int k;
    private int dim;
    private Kluster[] clusters;
    private int maxIter = 100;
    private double tol = 1e-4;
    private Random rnd = new Random(42);
    private Distance distance = Distance.EUCLIDEAN;
    private int[] labels;

    public KMeans(int k, int dim) {
        if (k <= 0) throw new IllegalArgumentException("k must be > 0");
        this.k = k;
        this.dim = dim;
        this.clusters = new Kluster[k];
    }

    public void setMaxIter(int m) { this.maxIter = m; }
    public void setTol(double t) { this.tol = t; }
    public void setSeed(long s) { this.rnd = new Random(s); }
    public void setDistance(Distance d) { this.distance = d; }

    @Override
    public void fit(List<double[]> data, boolean useKpp) {
        if (data == null || data.isEmpty()) return;
        int n = data.size();
        labels = new int[n];
        if (useKpp) initKMeansPP(data); else initRandom(data);

        boolean changed = true;
        int iter = 0;
        while (changed && iter < maxIter) {
            changed = false;
            for (Kluster c : clusters) c.clearMembers();
            for (int i = 0; i < n; i++) {
                double[] x = data.get(i);
                int b = predict(x);
                labels[i] = b;
                clusters[b].addMember(x);
            }
            for (Kluster c : clusters) if (c.recomputeCentroid(tol)) changed = true;
            iter++;
        }
    }

    private void initRandom(List<double[]> data) {
        for (int i = 0; i < k; i++) clusters[i] = new Kluster(data.get(rnd.nextInt(data.size())));
    }

    private void initKMeansPP(List<double[]> data) {
        int n = data.size();
        List<double[]> centroids = new ArrayList<>();
        centroids.add(data.get(rnd.nextInt(n)));
        double[] distances = new double[n];
        for (int c = 1; c < k; c++) {
            double sum = 0;
            for (int i = 0; i < n; i++) {
                double[] x = data.get(i);
                double best = Double.MAX_VALUE;
                for (double[] cent : centroids) { double d = distance(x, cent); if (d < best) best = d; }
                distances[i] = best;
                sum += best;
            }
            double r = rnd.nextDouble() * sum;
            double acc = 0;
            int idx = 0;
            for (int i = 0; i < n; i++) { acc += distances[i]; if (acc >= r) { idx = i; break; } }
            centroids.add(data.get(idx));
        }
        for (int i = 0; i < k; i++) clusters[i] = new Kluster(centroids.get(i));
    }

    @Override
    public int predict(double[] x) {
        int best = -1;
        double bestDist = Double.MAX_VALUE;
        for (int i = 0; i < k; i++) {
            double d = distance(x, clusters[i].getCentroid());
            if (d < bestDist) { bestDist = d; best = i; }
        }
        return best;
    }

    @Override
    public Kluster[] getClusters() { return clusters; }

    @Override
    public int[] getLabels() { return labels; }

    @Override
    public double silhouette(List<double[]> data) {
        if (labels == null || data == null) return Double.NaN;
        int n = data.size(); if (k <= 1) return Double.NaN;
        double total = 0;
        for (int i = 0; i < n; i++) {
            double[] xi = data.get(i);
            int ci = labels[i];
            double a = 0; int aCnt = 0;
            for (int j = 0; j < n; j++) if (labels[j] == ci && j != i) { a += distance(xi, data.get(j)); aCnt++; }
            if (aCnt > 0) a /= aCnt;
            double b = Double.MAX_VALUE;
            for (int c = 0; c < k; c++) if (c != ci) {
                double sum = 0; int cnt = 0;
                for (int j = 0; j < n; j++) if (labels[j] == c) { sum += distance(xi, data.get(j)); cnt++; }
                if (cnt > 0) { double avg = sum / cnt; if (avg < b) b = avg; }
            }
            double denom = Math.max(a, b);
            double s = (denom == 0) ? 0 : (b - a) / denom;
            total += s;
        }
        return total / n;
    }

    @Override
    public String getName() { return "KMeans"; }

    private double distance(double[] a, double[] b) { return (distance == Distance.MANHATTAN) ? manhattan(a,b) : euclidean(a,b); }
    private double euclidean(double[] a, double[] b) { double s = 0; for (int i = 0; i < dim; i++) { double d = a[i] - b[i]; s += d*d; } return Math.sqrt(s); }
    private double manhattan(double[] a, double[] b) { double s = 0; for (int i = 0; i < dim; i++) s += Math.abs(a[i]-b[i]); return s; }
}
