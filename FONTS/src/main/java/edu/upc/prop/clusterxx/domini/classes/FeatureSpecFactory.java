package edu.upc.prop.clusterxx.domini.classes;

import java.util.ArrayList;
import java.util.List;

/**
 * Fábrica para construir FeatureSpec[] a partir de Pregunta(s).
 * Mapea TipusPregunta y metadatos (orden, rangos, max selecciones) a DistanceCalculator.FeatureSpec.
 */
public class FeatureSpecFactory {

    /**
     * Construeix un FeatureSpec per cada Pregunta, en el mateix ordre.
     * 
     * @param preguntas Llista de preguntes a convertir en especificacions
     * @return Array de FeatureSpec corresponent a cada pregunta
     */
    public static DistanceCalculator.FeatureSpec[] fromPreguntas(List<Pregunta> preguntas) {
        if (preguntas == null) return new DistanceCalculator.FeatureSpec[0];
        DistanceCalculator.FeatureSpec[] specs = new DistanceCalculator.FeatureSpec[preguntas.size()];
        for (int i = 0; i < preguntas.size(); i++) specs[i] = fromPregunta(preguntas.get(i));
        return specs;
    }

    /**
     * Construeix el FeatureSpec per a una única Pregunta.
     * 
     * Mapeja el tipus de pregunta i els seus metadatos a l'especificació corresponent:
     * - NUMERICA: Utilitza els valors mínim i màxim
     * - QUALITATIVA_ORDENADA: Utilitza l'ordre de les opcions i la cardinalitat
     * - QUALITATIVA_NO_ORDENADA_SIMPLE: Especificació nominal simple
     * - QUALITATIVA_NO_ORDENADA_MULTIPLE: Especificació nominal múltiple amb màxim de seleccions
     * - TEXT_LLIURE: Especificació de text lliure
     * 
     * @param p La pregunta a convertir en especificació
     * @return FeatureSpec corresponent al tipus de pregunta
     * @throws IllegalArgumentException si la pregunta és null
     */
    public static DistanceCalculator.FeatureSpec fromPregunta(Pregunta p) {
        if (p == null) throw new IllegalArgumentException("Pregunta cannot be null");
        switch (p.getTipus()) {
            case NUMERICA:
                // Si existen rangos definidos, los incluimos
                return DistanceCalculator.FeatureSpec.numeric(p.getValorMinim(), p.getValorMaxim());
            case QUALITATIVA_ORDENADA:
                // El orden es el orden de las opciones definidas
                List<String> orden = new ArrayList<>();
                for (Opcio o : p.getOpcions()) orden.add(o.getText());
                // m = número de modalidades
                Integer m = p.getOpcions() != null ? p.getOpcions().size() : null;
                return DistanceCalculator.FeatureSpec.ordinal(orden, m);
            case QUALITATIVA_NO_ORDENADA_SIMPLE:
                return DistanceCalculator.FeatureSpec.nominalSingle();
            case QUALITATIVA_NO_ORDENADA_MULTIPLE:
                return DistanceCalculator.FeatureSpec.nominalMulti(p.getMaxSeleccions());
            case TEXT_LLIURE:
            default:
                return DistanceCalculator.FeatureSpec.freeText();
        }
    }
}
