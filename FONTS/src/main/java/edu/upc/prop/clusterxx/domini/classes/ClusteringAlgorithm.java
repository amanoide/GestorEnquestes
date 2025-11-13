package edu.upc.prop.clusterxx.domini.classes;

import java.util.List;

/**
 * Interfaz común para algoritmos de clustering en el paquete domini.
 */
public interface ClusteringAlgorithm {
    /** Ajusta el modelo con los datos provistos. */
    void fit(List<double[]> data, boolean useKpp);

    /** Versión por defecto de fit que no usa kpp. */
    default void fit(List<double[]> data) { fit(data, false); }

    /** Predice el índice de clúster más cercano para el vector x. */
    int predict(double[] x);

    /** Devuelve las etiquetas asignadas tras el fit. */
    int[] getLabels();

    /** Devuelve los clústers (centroides + miembros) tras el fit. */
    Kluster[] getClusters();

    /** Calcula la puntuación Silhouette media para los datos, o NaN si no procede. */
    double silhouette(List<double[]> data);

    /** Nombre del algoritmo (por ejemplo "KMeans"). */
    String getName();
}
