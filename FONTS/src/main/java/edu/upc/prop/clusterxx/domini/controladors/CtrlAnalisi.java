package edu.upc.prop.clusterxx.domini.controladors;

import edu.upc.prop.clusterxx.domini.classes.*;

import java.util.List;

/**
 * Controlador de análisis para clustering de usuarios por respuestas (todos String).
 * Requiere FeatureSpec[] para calcular distancias por dimensión según tipo de variable.
 */
public class CtrlAnalisi {

    /**
     * Ejecuta clustering KMeans o KMeans++ sobre datos vectorizados (String[]).
     * @param data Lista de puntos, cada punto es un array de String
     * @param k número de clústeres
     * @param usePlusPlus true para inicialización KMeans++
     * @param maxIters máximo de iteraciones
     * @param specs especificación por dimensión del tipo de variable
     * @return lista de clústeres con centroides y miembros
     */
    public List<Kluster> cluster(List<String[]> data, int k, boolean usePlusPlus, int maxIters, DistanceCalculator.FeatureSpec[] specs) {
        if (usePlusPlus) return new KMeansPlusPlus().fit(data, k, maxIters, specs);
        return new KMeans().fit(data, k, maxIters, specs);
    }

    /**
     * Helper para construir un punto a partir de valores String.
     * Todos los valores deben ser String.
     */
    public String[] buildPoint(String... values) {
        return values;
    }

    /** Construye los FeatureSpec[] desde una lista de Preguntas del dominio (mismo orden). */
    public DistanceCalculator.FeatureSpec[] buildSpecsFromPreguntas(List<Pregunta> preguntas) {
        return FeatureSpecFactory.fromPreguntas(preguntas);
    }
}
