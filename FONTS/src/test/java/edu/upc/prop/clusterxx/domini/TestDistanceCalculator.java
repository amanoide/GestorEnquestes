package edu.upc.prop.clusterxx.domini;

import edu.upc.prop.clusterxx.domini.classes.DistanceCalculator;
import edu.upc.prop.clusterxx.domini.classes.DistanceCalculator.FeatureSpec;
import edu.upc.prop.clusterxx.domini.classes.DistanceCalculator.VariableKind;

import static org.junit.Assert.*;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Tests per la classe DistanceCalculator.
 */
public class TestDistanceCalculator {

    private final DistanceCalculator dc = new DistanceCalculator();

    /* -------------------- Helpers -------------------- */

    private FeatureSpec[] specs(FeatureSpec... specs) {
        return specs;
    }

    private FeatureSpec numeric() {
        return FeatureSpec.numeric(0.0, 10.0);
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

    private FeatureSpec nominalSingle() {
        return FeatureSpec.nominalSingle();
    }

    private FeatureSpec nominalMulti() {
        return FeatureSpec.nominalMulti();
    }

    private FeatureSpec freeText() {
        return FeatureSpec.freeText();
    }

        /*  ===========================================
            TESTS distance() CLASSE DistanceCalculator
        =============================================== */

    /**
     * Rebutja la comparació quan algun vector és nul ja que no es poden calcular distàncies.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDistanceAmbNulls() {
        dc.distance(null, new String[]{"1"}, specs(numeric()));
    }

    /**
     * Les dimensions dels vectors han de coincidir; altrament cal llençar una excepció.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDistanceDimensionsIncorrectes() {
        dc.distance(new String[]{"1"}, new String[]{"1", "2"}, specs(numeric()));
    }

    /**
     * Sense rang definit les especificacions numèriques han de llençar una excepció.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDistanceNumericSenseRang() {
        FeatureSpec spec = new FeatureSpec(VariableKind.NUMERIC);
        dc.distance(new String[]{"1"}, new String[]{"4"}, specs(spec));
    }

    /**
     * Amb normalització els valors s'han d'escalar al rang indicat.
     */
    @Test
    public void testDistanceNumericNormalitzat() {
        double d = dc.distance(new String[]{"1"}, new String[]{"4"}, specs(numeric(0, 10)));
        assertEquals(0.3, d, 1e-9);
    }

    /**
     * Un rang degenerat (min = max) ha de llençar una excepció per evitar divisions per zero.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDistanceNumericMinIgualMaxLlencaExcepcio() {
        dc.distance(new String[]{"1"}, new String[]{"4"}, specs(numeric(5, 5)));
    }

    /**
     * Les distàncies mixtes han de combinar les contribucions de totes les dimensions.
     */
    @Test
    public void testDistanceMezclaDimensions() {
        FeatureSpec[] specs = specs(numeric(), ordinal(Arrays.asList("baix", "mig", "alt")), nominalSingle());
        String[] a = {"0", "baix", "A"};
        String[] b = {"3", "alt", "B"};

        double dist = dc.distance(a, b, specs);
        assertTrue("La distància ha de ser positiva", dist > 0);
    }

        /*  ==============================================
            TESTS distanceManhattan() CLASSE DistanceCalculator
        ================================================== */

    /**
     * La distància Manhattan ha de sumar les diferències normalitzades de cada dimensió.
     */
    @Test
    public void testDistanceManhattanBase() {
        double d = dc.distanceManhattan(new String[]{"1", "baix"}, new String[]{"2", "alt"},
            specs(numeric(), ordinal(Arrays.asList("baix", "alt"))));
        // numeric local diff = 1/(10) = 0.1 -> 0.05 after dividir per N; ordinal = 1 -> 0.5
        assertEquals(0.55, d, 1e-9);
    }

    /**
     * També en la distància Manhattan les dimensions han de coincidir exactament.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDistanceManhattanDimensionsIncorrectes() {
        dc.distanceManhattan(new String[]{"1"}, new String[]{"1", "2"}, specs(numeric()));
    }

        /*  ================================================
            TESTS distanceNumeric() CLASSE DistanceCalculator
        ==================================================== */

    /**
     * Les components numèriques nul·les s'han de tractar com a valors mancant amb distància màxima.
     */
    @Test
    public void testDistanceNumericNulls() {
        FeatureSpec spec = numeric();
        double d = dc.distance(new String[]{null}, new String[]{"1"}, specs(spec));
        assertEquals(1.0, d, 1e-9);
    }

    /**
     * Els valors no parsejables també han de produir la distància màxima.
     */
    @Test
    public void testDistanceNumericNoParsejable() {
        double d = dc.distance(new String[]{"abc"}, new String[]{"2"}, specs(numeric()));
        assertEquals(1.0, d, 1e-9);
    }

        /*  =================================================
            TESTS distanceOrdinal() CLASSE DistanceCalculator
        ===================================================== */

    /**
     * Sense ordre definit el càlcul ha de llençar una excepció perquè falta informació.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDistanceOrdinalSenseOrdre() {
        dc.distance(new String[]{"A"}, new String[]{"B"}, specs(ordinal(null)));
    }

    /**
     * Quan hi ha ordre, la distància s'ha d'escalar segons la separació relativa.
     */
    @Test
    public void testDistanceOrdinalAmbOrdre() {
        FeatureSpec spec = ordinal(Arrays.asList("A", "B", "C"));
        double d = dc.distance(new String[]{"A"}, new String[]{"C"}, specs(spec));
        // diff=2, m=3 -> (2)/(3-1)=1
        assertEquals(1.0, d, 1e-9);
    }

    /**
     * Si el valor no existeix a l'ordre definit s'ha de considerar una diferència màxima.
     */
    @Test
    public void testDistanceOrdinalForaOrdre() {
        FeatureSpec spec = ordinal(Arrays.asList("A", "B"));
        double d = dc.distance(new String[]{"A"}, new String[]{"Z"}, specs(spec));
        assertEquals(1.0, d, 1e-9);
    }

    /**
     * El paràmetre M explícit ha de normalitzar la distància encara que hi hagi poques opcions.
     */
    @Test
    public void testDistanceOrdinalAmbMExplicit() {
        FeatureSpec spec = ordinal(Arrays.asList("A", "B", "C"), 5);
        double d = dc.distance(new String[]{"A"}, new String[]{"C"}, specs(spec));
        assertEquals(0.5, d, 1e-9); // diff 2 / (5-1)
    }

    /**
     * Sense llista encara que hi hagi M definit s'ha de llençar una excepció.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDistanceOrdinalSenseLlistaAmbM() {
        FeatureSpec spec = new FeatureSpec(VariableKind.ORDINAL, null, 4, null, null, null);
        dc.distance(new String[]{"X"}, new String[]{"Y"}, specs(spec));
    }

        /*  =======================================================
            TESTS distanceNominalSingle() CLASSE DistanceCalculator
        =========================================================== */

    /**
     * Les respostes iguals en nominal simple han de donar distància zero.
     */
    @Test
    public void testNominalSingleIguals() {
        double d = dc.distance(new String[]{"A"}, new String[]{"A"}, specs(nominalSingle()));
        assertEquals(0.0, d, 1e-9);
    }

    /**
     * Valors diferents en nominal simple representen la distància màxima.
     */
    @Test
    public void testNominalSingleDistints() {
        double d = dc.distance(new String[]{"A"}, new String[]{"B"}, specs(nominalSingle()));
        assertEquals(1.0, d, 1e-9);
    }

    /**
     * La manca de valor també s'ha de considerar diferent d'un valor definit.
     */
    @Test
    public void testNominalSingleNull() {
        double d = dc.distance(new String[]{null}, new String[]{"B"}, specs(nominalSingle()));
        assertEquals(1.0, d, 1e-9);
    }

        /*  ======================================================
            TESTS distanceNominalMulti() CLASSE DistanceCalculator
        =========================================== */

    /**
     * En nominal múltiple la mateixa bossa d'etiquetes ha de donar distància zero.
     */
    @Test
    public void testNominalMultiIguals() {
        double d = dc.distance(new String[]{"A,B"}, new String[]{"B,A"}, specs(nominalMulti()));
        assertEquals(0.0, d, 1e-9);
    }

    /**
     * La distància parcial ha de seguir la mètrica de Jaccard (intersecció/uniò).
     */
    @Test
    public void testNominalMultiParcial() {
        double d = dc.distance(new String[]{"A,B"}, new String[]{"B,C"}, specs(nominalMulti()));
        // Jaccard -> inter=1, union=3 => 1 - 1/3 = 2/3
        assertEquals(2.0 / 3.0, d, 1e-9);
    }

    /**
     * Dues respostes buides s'han de considerar equivalents.
     */
    @Test
    public void testNominalMultiBuids() {
        double d = dc.distance(new String[]{""}, new String[]{""}, specs(nominalMulti()));
        assertEquals(0.0, d, 1e-9);
    }

    /**
     * Si una resposta és nul·la la distància s'ha de considerar màxima.
     */
    @Test
    public void testNominalMultiAmbNull() {
        double d = dc.distance(new String[]{null}, new String[]{"A"}, specs(nominalMulti()));
        assertEquals(1.0, d, 1e-9);
    }

        /*  ==================================================
            TESTS distanceFreeText() CLASSE DistanceCalculator
        ====================================================== */

    /**
     * El text lliure idèntic ha de retornar distància zero.
     */
    @Test
    public void testFreeTextIdentic() {
        double d = dc.distance(new String[]{"hola"}, new String[]{"hola"}, specs(freeText()));
        assertEquals(0.0, d, 1e-9);
    }

    /**
     * Textos completament diferents han de produir distància màxima.
     */
    @Test
    public void testFreeTextCompletamentDistint() {
        double d = dc.distance(new String[]{"a"}, new String[]{"bcd"}, specs(freeText()));
        assertEquals(1.0, d, 1e-9);
    }

    /**
     * Variacions parcials s'han de reflectir amb valors intermedis entre 0 i 1.
     */
    @Test
    public void testFreeTextParcial() {
        double d = dc.distance(new String[]{"gat"}, new String[]{"got"}, specs(freeText()));
        assertTrue("La distància ha de ser entre 0 i 1", d > 0 && d < 1);
    }

    /**
     * El text nul s'ha d'interpretar com a diferent de qualsevol cadena no nul·la.
     */
    @Test
    public void testFreeTextNull() {
        double d = dc.distance(new String[]{null}, new String[]{"b"}, specs(freeText()));
        assertEquals(1.0, d, 1e-9);
    }

        /*  ============================================================
            TESTS distanceManhattan() combinat CLASSE DistanceCalculator
        ================================================================ */

    /**
     * La distància Manhattan combinada ha d'agregar correctament diferents tipus de variables.
     */
    @Test
    public void testDistanceManhattanMescla() {
        FeatureSpec[] specs = specs(numeric(), nominalSingle(), freeText());
        String[] a = {"0", "A", "hola"};
        String[] b = {"5", "B", "halo"};
        double d = dc.distanceManhattan(a, b, specs);
        assertTrue(d > 0);
    }
}
