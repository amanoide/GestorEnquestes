package edu.upc.prop.clusterxx.domini;

import edu.upc.prop.clusterxx.domini.classes.DistanceCalculator;
import edu.upc.prop.clusterxx.domini.classes.FeatureSpecFactory;
import edu.upc.prop.clusterxx.domini.classes.Opcio;
import edu.upc.prop.clusterxx.domini.classes.Pregunta;
import edu.upc.prop.clusterxx.domini.classes.TipusPregunta;

import static org.junit.Assert.*;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Tests de la classe FeatureSpecFactory.
 */
public class TestFeatureSpecFactory {

    /* -------------------- Helpers -------------------- */

    private Pregunta preguntaNumerica(Double min, Double max) {
        return new Pregunta("Q1", "Num", min, max);
    }

    private Pregunta preguntaOrdenada(String... opcions) {
        Pregunta p = new Pregunta("Q2", "Ordenada", TipusPregunta.QUALITATIVA_ORDENADA, 1);
        int id = 0;
        for (String text : opcions) {
            p.afegirOpcio(new Opcio(id, text, id)); // Añadir orden explícito
            id++;
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

        /*  ==============================================
            TESTS fromPregunta() CLASSE FeatureSpecFactory
        =================================================== */

    /**
     * No es pot construir una FeatureSpec a partir d'una pregunta nul·la.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFromPreguntaNull() {
        FeatureSpecFactory.fromPregunta(null);
    }

    /**
     * Les preguntes numèriques amb rang han de generar especificacions amb min i max.
     */
    @Test
    public void testPreguntaNumericaAmbRang() {
        DistanceCalculator.FeatureSpec spec = FeatureSpecFactory.fromPregunta(preguntaNumerica(0.0, 10.0));
        assertEquals(DistanceCalculator.VariableKind.NUMERIC, spec.kind);
        assertEquals(Double.valueOf(0.0), spec.numericMin);
        assertEquals(Double.valueOf(10.0), spec.numericMax);
    }

    /**
     * Si no hi ha rang numèric definit, els límits han de quedar a null.
     */
    @Test
    public void testPreguntaNumericaSenseRang() {
        DistanceCalculator.FeatureSpec spec = FeatureSpecFactory.fromPregunta(preguntaNumerica(null, null));
        assertNull(spec.numericMin);
        assertNull(spec.numericMax);
    }

    /**
     * Les preguntes ordenades s'han de mapejar a variables ordinales amb l'ordre corresponent.
     */
    @Test
    public void testPreguntaOrdenada() {
        Pregunta p = preguntaOrdenada("baix", "mig", "alt");
        DistanceCalculator.FeatureSpec spec = FeatureSpecFactory.fromPregunta(p);
        assertEquals(DistanceCalculator.VariableKind.ORDINAL, spec.kind);
        assertEquals(Arrays.asList("baix", "mig", "alt"), spec.ordinalOrder);
        assertEquals(Integer.valueOf(3), spec.ordinalCardinality);
    }

    /**
     * Les preguntes qualitatives simples han de generar un domini amb totes les opcions.
     */
    @Test
    public void testPreguntaNominalSimple() {
        Pregunta p = preguntaNominalSimple("vermell", "verd");
        DistanceCalculator.FeatureSpec spec = FeatureSpecFactory.fromPregunta(p);
        assertEquals(DistanceCalculator.VariableKind.NOMINAL_SINGLE, spec.kind);
        assertNull(spec.maxSelections);
    }

    /**
     * Si la pregunta qualitativa no té opcions, el domini ha de quedar buit.
     */
    @Test
    public void testPreguntaNominalSenseOpcions() {
        Pregunta p = preguntaNominalSimple();
        DistanceCalculator.FeatureSpec spec = FeatureSpecFactory.fromPregunta(p);
        assertEquals(DistanceCalculator.VariableKind.NOMINAL_SINGLE, spec.kind);
    }

    /**
     * Les preguntes múltiples han de capturar el màxim de seleccions i totes les opcions.
     */
    @Test
    public void testPreguntaNominalMultiple() {
        Pregunta p = preguntaNominalMultiple(2, "groc", "blau", "negre");
        DistanceCalculator.FeatureSpec spec = FeatureSpecFactory.fromPregunta(p);
        assertEquals(DistanceCalculator.VariableKind.NOMINAL_MULTI, spec.kind);
        assertEquals(Integer.valueOf(2), spec.maxSelections);
    }

    /**
     * Les preguntes de text lliure han de produir el tipus FREE_TEXT.
     */
    @Test
    public void testPreguntaTextLliure() {
        DistanceCalculator.FeatureSpec spec = FeatureSpecFactory.fromPregunta(preguntaText());
        assertEquals(DistanceCalculator.VariableKind.FREE_TEXT, spec.kind);
    }

        /*  ===============================================
            TESTS fromPreguntas() CLASSE FeatureSpecFactory
        =================================================== */

    /**
     * La fàbrica ha de manejar col·leccions nul·les retornant un array buit.
     */
    @Test
    public void testFromPreguntasNull() {
        DistanceCalculator.FeatureSpec[] specs = FeatureSpecFactory.fromPreguntas(null);
        assertEquals(0, specs.length);
    }

    /**
     * Es comprova que cada tipus de pregunta es converteix a la especificació correcta.
     */
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

    /**
     * Si la llista conté algun element nul s'ha de llençar una excepció.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFromPreguntasAmbElementNull() {
        List<Pregunta> preguntes = Arrays.asList(preguntaNumerica(0.0, 1.0), null);
        FeatureSpecFactory.fromPreguntas(preguntes);
    }
}
