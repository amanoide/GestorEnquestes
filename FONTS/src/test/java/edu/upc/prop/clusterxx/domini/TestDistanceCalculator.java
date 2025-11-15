package edu.upc.prop.clusterxx.domini;

import edu.upc.prop.clusterxx.domini.classes.DistanceCalculator;
import edu.upc.prop.clusterxx.domini.classes.DistanceCalculator.FeatureSpec;
import edu.upc.prop.clusterxx.domini.classes.DistanceCalculator.VariableKind;

import static org.junit.Assert.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Tests exhaustius per la classe DistanceCalculator.
 */
public class TestDistanceCalculator {

    private final DistanceCalculator dc = new DistanceCalculator();

    @BeforeClass
    public static void iniTests() {
        System.out.println("Iniciant els tests de DistanceCalculator.\n");
    }

    @AfterClass
    public static void fiTests() {
        System.out.println("Finalitzats els tests de DistanceCalculator.\n");
    }

    /* -------------------- Helpers -------------------- */

    private FeatureSpec[] specs(FeatureSpec... specs) {
        return specs;
    }

    private FeatureSpec numeric() {
        return FeatureSpec.numeric();
    }

    private FeatureSpec numeric(double min, double max) {
        return FeatureSpec.numeric(min, max);
    }

    private FeatureSpec ordinal(List<String> order) {
        return FeatureSpec.ordinal(order);
    }

    private FeatureSpec ordinal(List<String> order, int m) {
        return FeatureSpec.ordinal(order, m);
    }

    private FeatureSpec ordinalWith(int m) {
        return FeatureSpec.ordinalWithM(m);
    }

    private FeatureSpec nominalSingle() {
        return FeatureSpec.nominalSingle(new HashSet<>());
    }

    private FeatureSpec nominalMulti() {
        return FeatureSpec.nominalMulti(new HashSet<>());
    }

    private FeatureSpec freeText() {
        return FeatureSpec.freeText();
    }

    /* -------------------- distance() -------------------- */

    @Test(expected = IllegalArgumentException.class)
    public void testDistanceAmbNulls() {
        dc.distance(null, new String[]{"1"}, specs(numeric()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDistanceDimensionsIncorrectes() {
        dc.distance(new String[]{"1"}, new String[]{"1", "2"}, specs(numeric()));
    }

    @Test
    public void testDistanceNumericSenseNormalitzar() {
        double d = dc.distance(new String[]{"1"}, new String[]{"4"}, specs(numeric()));
        assertEquals(3.0, d, 1e-9);
    }

    @Test
    public void testDistanceNumericNormalitzat() {
        double d = dc.distance(new String[]{"1"}, new String[]{"4"}, specs(numeric(0, 10)));
        assertEquals(0.3, d, 1e-9);
    }

    @Test
    public void testDistanceMezclaDimensions() {
        FeatureSpec[] specs = specs(numeric(), ordinal(Arrays.asList("baix", "mig", "alt")), nominalSingle());
        String[] a = {"0", "baix", "A"};
        String[] b = {"3", "alt", "B"};

        double dist = dc.distance(a, b, specs);
        assertTrue("La distància ha de ser positiva", dist > 0);
    }

    /* -------------------- distanceManhattan() -------------------- */

    @Test
    public void testDistanceManhattanBase() {
        double d = dc.distanceManhattan(new String[]{"1", "baix"}, new String[]{"2", "alt"},
            specs(numeric(), ordinal(Arrays.asList("baix", "alt"))));
        // numeric diff = 1, normalized by N=2 -> 0.5; ordinal diff = 1/(2-1)=1, normalized by N=2 -> 0.5; sum=1
        assertEquals(1.0, d, 1e-9);
    }

    /* -------------------- distanceNumeric() -------------------- */

    @Test
    public void testDistanceNumericNulls() {
        FeatureSpec spec = new FeatureSpec(VariableKind.NUMERIC);
        double d = dc.distance(new String[]{null}, new String[]{"1"}, specs(spec));
        assertEquals(1.0, d, 1e-9);
    }

    @Test
    public void testDistanceNumericNoParsejable() {
        double d = dc.distance(new String[]{"abc"}, new String[]{"2"}, specs(numeric()));
        assertEquals(1.0, d, 1e-9);
    }

    /* -------------------- distanceOrdinal() -------------------- */

    @Test
    public void testDistanceOrdinalSenseOrdre() {
        double d = dc.distance(new String[]{"A"}, new String[]{"B"}, specs(ordinal(null)));
        assertEquals(1.0, d, 1e-9);
    }

    @Test
    public void testDistanceOrdinalAmbOrdre() {
        FeatureSpec spec = ordinal(Arrays.asList("A", "B", "C"));
        double d = dc.distance(new String[]{"A"}, new String[]{"C"}, specs(spec));
        // diff=2, m=3 -> (2)/(3-1)=1
        assertEquals(1.0, d, 1e-9);
    }

    @Test
    public void testDistanceOrdinalForaOrdre() {
        FeatureSpec spec = ordinal(Arrays.asList("A", "B"));
        double d = dc.distance(new String[]{"A"}, new String[]{"Z"}, specs(spec));
        assertEquals(1.0, d, 1e-9);
    }

    @Test
    public void testDistanceOrdinalAmbMExplicit() {
        FeatureSpec spec = ordinal(Arrays.asList("A", "B", "C"), 5);
        double d = dc.distance(new String[]{"A"}, new String[]{"C"}, specs(spec));
        assertEquals(0.5, d, 1e-9); // diff 2 / (5-1)
    }

    @Test
    public void testDistanceOrdinalSenseLlistaAmbM() {
        FeatureSpec spec = ordinalWith(4);
        double d = dc.distance(new String[]{"X"}, new String[]{"Y"}, specs(spec));
        // sense ordre, es compara per igualtat -> 1.0
        assertEquals(1.0, d, 1e-9);
    }

    /* -------------------- distanceNominalSingle() -------------------- */

    @Test
    public void testNominalSingleIguals() {
        double d = dc.distance(new String[]{"A"}, new String[]{"A"}, specs(nominalSingle()));
        assertEquals(0.0, d, 1e-9);
    }

    @Test
    public void testNominalSingleDistints() {
        double d = dc.distance(new String[]{"A"}, new String[]{"B"}, specs(nominalSingle()));
        assertEquals(1.0, d, 1e-9);
    }

    @Test
    public void testNominalSingleNull() {
        double d = dc.distance(new String[]{null}, new String[]{"B"}, specs(nominalSingle()));
        assertEquals(1.0, d, 1e-9);
    }

    /* -------------------- distanceNominalMulti() -------------------- */

    @Test
    public void testNominalMultiIguals() {
        double d = dc.distance(new String[]{"A,B"}, new String[]{"B,A"}, specs(nominalMulti()));
        assertEquals(0.0, d, 1e-9);
    }

    @Test
    public void testNominalMultiParcial() {
        double d = dc.distance(new String[]{"A,B"}, new String[]{"B,C"}, specs(nominalMulti()));
        // Jaccard -> inter=1, union=3 => 1 - 1/3 = 2/3
        assertEquals(2.0 / 3.0, d, 1e-9);
    }

    @Test
    public void testNominalMultiBuids() {
        double d = dc.distance(new String[]{""}, new String[]{""}, specs(nominalMulti()));
        assertEquals(0.0, d, 1e-9);
    }

    @Test
    public void testNominalMultiAmbNull() {
        double d = dc.distance(new String[]{null}, new String[]{"A"}, specs(nominalMulti()));
        assertEquals(1.0, d, 1e-9);
    }

    /* -------------------- distanceFreeText() -------------------- */

    @Test
    public void testFreeTextIdentic() {
        double d = dc.distance(new String[]{"hola"}, new String[]{"hola"}, specs(freeText()));
        assertEquals(0.0, d, 1e-9);
    }

    @Test
    public void testFreeTextCompletamentDistint() {
        double d = dc.distance(new String[]{"a"}, new String[]{"bcd"}, specs(freeText()));
        assertEquals(1.0, d, 1e-9);
    }

    @Test
    public void testFreeTextParcial() {
        double d = dc.distance(new String[]{"gat"}, new String[]{"gató"}, specs(freeText()));
        assertTrue("La distància ha de ser entre 0 i 1", d > 0 && d < 1);
    }

    @Test
    public void testFreeTextNull() {
        double d = dc.distance(new String[]{null}, new String[]{"b"}, specs(freeText()));
        assertEquals(1.0, d, 1e-9);
    }

    /* -------------------- distanceManhattan combinat -------------------- */

    @Test
    public void testDistanceManhattanMescla() {
        FeatureSpec[] specs = specs(numeric(), nominalSingle(), freeText());
        String[] a = {"0", "A", "hola"};
        String[] b = {"5", "B", "halo"};
        double d = dc.distanceManhattan(a, b, specs);
        assertTrue(d > 0);
    }
}
