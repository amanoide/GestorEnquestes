package edu.upc.prop.clusterxx.domini.controladors;

import java.util.List;

import edu.upc.prop.clusterxx.domini.classes.ClusteringAlgorithm;
import edu.upc.prop.clusterxx.domini.classes.KMeans;

/**
 * Controlador simple que actúa como fachada para ejecutar algoritmos de clustering.
 */
public class CtrlAnalisi {
    private KMeans kmeans;

    public CtrlAnalisi() { }

    /** Ejecuta KMeans con parámetros básicos y devuelve las etiquetas. */
    public int[] executarKMeans(int k, List<double[]> data) {
        return executarKMeans(k, data, false, KMeans.Distance.EUCLIDEAN, 42L);
    }

    /** Ejecuta KMeans con opciones avanzadas. */
    public int[] executarKMeans(int k, List<double[]> data, boolean useKpp, KMeans.Distance dist, long seed) {
        if (data == null || data.isEmpty()) return new int[0];
        int dim = data.get(0).length;
        KMeans km = new KMeans(k, dim);
        km.setSeed(seed); km.setDistance(dist); km.fit(data, useKpp);
        this.kmeans = km;
        return km.getLabels();
    }

    /** Ejecuta cualquier algoritmo que implemente ClusteringAlgorithm. */
    public int[] executar(ClusteringAlgorithm algorithm, List<double[]> data, boolean useKpp) {
        if (algorithm == null || data == null || data.isEmpty()) return new int[0];
        algorithm.fit(data, useKpp);
        if (algorithm instanceof KMeans) this.kmeans = (KMeans) algorithm;
        return algorithm.getLabels();
    }

    public KMeans getKMeans() { return kmeans; }

    public double silhouette(List<double[]> data) {
        if (kmeans == null) return Double.NaN;
        return kmeans.silhouette(data);
    }
}
