package edu.upc.prop.clusterxx.domini.classes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Fábrica para construir FeatureSpec[] a partir de Pregunta(s).
 * Mapea TipusPregunta y metadatos (orden, dominio, rangos, max selecciones) a DistanceCalculator.FeatureSpec.
 */
public class FeatureSpecFactory {

    /** Construye un FeatureSpec por cada Pregunta, en el mismo orden. */
    public static DistanceCalculator.FeatureSpec[] fromPreguntas(List<Pregunta> preguntas) {
        if (preguntas == null) return new DistanceCalculator.FeatureSpec[0];
        DistanceCalculator.FeatureSpec[] specs = new DistanceCalculator.FeatureSpec[preguntas.size()];
        for (int i = 0; i < preguntas.size(); i++) specs[i] = fromPregunta(preguntas.get(i));
        return specs;
    }

    /** Construye el FeatureSpec para una única Pregunta. */
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
                // Dominio: conjunto de opciones
                return DistanceCalculator.FeatureSpec.nominalSingle(toDomain(p));
            case QUALITATIVA_NO_ORDENADA_MULTIPLE:
                // Dominio + máximo de selecciones
                return DistanceCalculator.FeatureSpec.nominalMulti(toDomain(p), p.getMaxSeleccions());
            case TEXT_LLIURE:
            default:
                return DistanceCalculator.FeatureSpec.freeText();
        }
    }

    //Acabar de mirar que hace este método
    private static Set<String> toDomain(Pregunta p) {
        Set<String> domain = new HashSet<>();
        for (Opcio o : p.getOpcions()) domain.add(o.getText());
        return domain;
    }
}
