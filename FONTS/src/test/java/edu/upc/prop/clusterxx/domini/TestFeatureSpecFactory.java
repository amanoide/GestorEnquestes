package edu.upc.prop.clusterxx.domini;

import edu.upc.prop.clusterxx.domini.classes.DistanceCalculator;
import edu.upc.prop.clusterxx.domini.classes.FeatureSpecFactory;
import edu.upc.prop.clusterxx.domini.classes.Opcio;
import edu.upc.prop.clusterxx.domini.classes.Pregunta;
import edu.upc.prop.clusterxx.domini.classes.TipusPregunta;

import static org.junit.Assert.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Tests de la classe FeatureSpecFactory.
 */
public class TestFeatureSpecFactory {

    @BeforeClass
    public static void iniTests() {
        System.out.println("Iniciant els tests de FeatureSpecFactory.\n");
    }

    @AfterClass
    public static void fiTests() {
        System.out.println("Finalitzats els tests de FeatureSpecFactory.\n");
    }

    /* -------------------- Helpers -------------------- */

    private Pregunta preguntaNumerica(Double min, Double max) {
        return new Pregunta("Q1", "Num", min, max);
    }

    private Pregunta preguntaOrdenada(String... opcions) {
        Pregunta p = new Pregunta("Q2", "Ordenada", TipusPregunta.QUALITATIVA_ORDENADA, 1);
        int id = 0;
        for (String text : opcions) {
            p.afegirOpcio(new Opcio(id++, text));
        }
        return p;
    }

    private Pregunta preguntaNominalSimple(String... opcions) {
        Pregunta p = new Pregunta("Q3", "Simple", TipusPregunta.QUALITATIVA_NO_ORDENADA_SIMPLE, 1);
        int id = 0;
        for (String text : opcions) {
            p.afegirOpcio(new Opcio(id++, text));
        }
        return p;
    }

    private Pregunta preguntaNominalMultiple(int maxSeleccions, String... opcions) {
        Pregunta p = new Pregunta("Q4", "Multiple", TipusPregunta.QUALITATIVA_NO_ORDENADA_MULTIPLE, maxSeleccions);
        int id = 0;
        for (String text : opcions) {
            p.afegirOpcio(new Opcio(id++, text));
        }
        return p;
    }

    private Pregunta preguntaText() {
        return new Pregunta("Q5", "Text lliure");
    }

    /* -------------------- fromPregunta -------------------- */

    @Test(expected = IllegalArgumentException.class)
    public void testFromPreguntaNull() {
        FeatureSpecFactory.fromPregunta(null);
    }

    @Test
    public void testPreguntaNumericaAmbRang() {
        DistanceCalculator.FeatureSpec spec = FeatureSpecFactory.fromPregunta(preguntaNumerica(0.0, 10.0));
        assertEquals(DistanceCalculator.VariableKind.NUMERIC, spec.kind);
        assertEquals(Double.valueOf(0.0), spec.numericMin);
        assertEquals(Double.valueOf(10.0), spec.numericMax);
    }

    @Test
    public void testPreguntaOrdenada() {
        Pregunta p = preguntaOrdenada("baix", "mig", "alt");
        DistanceCalculator.FeatureSpec spec = FeatureSpecFactory.fromPregunta(p);
        assertEquals(DistanceCalculator.VariableKind.ORDINAL, spec.kind);
        assertEquals(Arrays.asList("baix", "mig", "alt"), spec.ordinalOrder);
        assertEquals(Integer.valueOf(3), spec.ordinalCardinality);
    }

    @Test
    public void testPreguntaNominalSimple() {
        Pregunta p = preguntaNominalSimple("vermell", "verd");
        DistanceCalculator.FeatureSpec spec = FeatureSpecFactory.fromPregunta(p);
        assertEquals(DistanceCalculator.VariableKind.NOMINAL_SINGLE, spec.kind);
        assertTrue(spec.domain.contains("vermell"));
        assertTrue(spec.domain.contains("verd"));
    }

    @Test
    public void testPreguntaNominalMultiple() {
        Pregunta p = preguntaNominalMultiple(2, "groc", "blau", "negre");
        DistanceCalculator.FeatureSpec spec = FeatureSpecFactory.fromPregunta(p);
        assertEquals(DistanceCalculator.VariableKind.NOMINAL_MULTI, spec.kind);
        assertEquals(Integer.valueOf(2), spec.maxSelections);
        assertTrue(spec.domain.contains("groc"));
        assertEquals(3, spec.domain.size());
    }

    @Test
    public void testPreguntaTextLliure() {
        DistanceCalculator.FeatureSpec spec = FeatureSpecFactory.fromPregunta(preguntaText());
        assertEquals(DistanceCalculator.VariableKind.FREE_TEXT, spec.kind);
    }

    /* -------------------- fromPreguntas -------------------- */

    @Test
    public void testFromPreguntasNull() {
        DistanceCalculator.FeatureSpec[] specs = FeatureSpecFactory.fromPreguntas(null);
        assertEquals(0, specs.length);
    }

    @Test
    public void testFromPreguntasDiverses() {
        List<Pregunta> preguntes = Arrays.asList(
            preguntaNumerica(1.0, 5.0),
            preguntaOrdenada("A", "B"),
            preguntaNominalSimple("X"),
            preguntaNominalMultiple(3, "Y", "Z"),
            preguntaText()
        );
        DistanceCalculator.FeatureSpec[] specs = FeatureSpecFactory.fromPreguntas(preguntes);
        assertEquals(5, specs.length);
        assertEquals(DistanceCalculator.VariableKind.NUMERIC, specs[0].kind);
        assertEquals(DistanceCalculator.VariableKind.ORDINAL, specs[1].kind);
        assertEquals(DistanceCalculator.VariableKind.NOMINAL_SINGLE, specs[2].kind);
        assertEquals(DistanceCalculator.VariableKind.NOMINAL_MULTI, specs[3].kind);
        assertEquals(DistanceCalculator.VariableKind.FREE_TEXT, specs[4].kind);
    }
}
