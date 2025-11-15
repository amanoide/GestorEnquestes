package edu.upc.prop.clusterxx.domini;

import edu.upc.prop.clusterxx.domini.classes.DistanceCalculator;
import edu.upc.prop.clusterxx.domini.classes.Kluster;

import static org.junit.Assert.*;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Tests de la classe Kluster.
 */
public class TestKluster {

    /**
     * Helper que crea especificacions numèriques per totes les dimensions.
     */
    private DistanceCalculator.FeatureSpec[] specsNumeriques(int dimensions) {
        DistanceCalculator.FeatureSpec[] specs = new DistanceCalculator.FeatureSpec[dimensions];
        for (int i = 0; i < dimensions; ++i) {
            specs[i] = DistanceCalculator.FeatureSpec.numeric(0.0, 10.0);
        }
        return specs;
    }

    /**
     * La constructora ha de guardar una còpia del centroid inicial.
     */
    @Test
    public void testConstructoraCentroidValid() {
        String[] seed = {"1", "2"};
        Kluster k = new Kluster(seed);

        assertArrayEquals(seed, k.getCentroid());
        assertEquals(0, k.size());
        assertTrue(k.getMembers().isEmpty());

        seed[0] = "99"; // el centroid intern no s'ha de modificar
        assertEquals("1", k.getCentroid()[0]);
    }

    /**
     * No es permet crear un klúster sense centroid o amb dimensions zero.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructoraCentroidNull() {
        new Kluster(null);
    }

    /**
     * També s'ha de impedir crear un centroid buit perquè no hi ha dimensions a processar.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructoraCentroidBuit() {
        new Kluster(new String[0]);
    }

    /**
     * El setter de centroid també ha de validar dimensions i fer còpies.
     */
    @Test
    public void testSetCentroid() {
        Kluster k = new Kluster(new String[]{"1", "2"});
        String[] nouCentroid = {"5", "6"};
        k.setCentroid(nouCentroid);

        assertArrayEquals(nouCentroid, k.getCentroid());
        nouCentroid[0] = "100";
        assertEquals("5", k.getCentroid()[0]);
    }

    /**
     * El nou centroid ha de coincidir en dimensions; si no coincideix s'ha de llençar una excepció.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetCentroidDimensionsIncorrectes() {
        Kluster k = new Kluster(new String[]{"1", "2"});
        k.setCentroid(new String[]{"només una"});
    }

    /**
     * Els membres s'han d'afegir copiats i la mida ha de coincidir.
     */
    @Test
    public void testAddMemberICopies() {
        Kluster k = new Kluster(new String[]{"0"});
        String[] membre = {"1"};
        k.addMember(membre);

        assertEquals(1, k.size());
        assertEquals("1", k.getMembers().get(0)[0]);

        membre[0] = "99";
        assertEquals("1", k.getMembers().get(0)[0]);
    }

    /**
     * getMembers ha de retornar una còpia per evitar modificacions externes.
     */
    @Test
    public void testGetMembersRetornaCopies() {
        Kluster k = new Kluster(new String[]{"A"});
        k.addMember(new String[]{"B"});

        List<String[]> members = k.getMembers();
        members.get(0)[0] = "C";

        assertEquals("B", k.getMembers().get(0)[0]);
        members.clear();
        assertEquals(1, k.size());
    }

    /**
     * clearMembers ha de deixar el klúster buit però mantenint el centroid.
     */
    @Test
    public void testClearMembers() {
        Kluster k = new Kluster(new String[]{"c"});
        k.addMember(new String[]{"c"});
        assertEquals(1, k.size());

        k.clearMembers();
        assertEquals(0, k.size());
        assertTrue(k.getMembers().isEmpty());
        assertEquals("c", k.getCentroid()[0]);
    }

    /**
     * Recompute en dimensions numèriques ha de calcular la mitjana i retornar true si canvia.
     */
    @Test
    public void testRecomputeCentroidNumeric() {
        Kluster k = new Kluster(new String[]{"0", "0"});
        k.addMember(new String[]{"0", "2"});
        k.addMember(new String[]{"2", "0"});
        k.addMember(new String[]{"2", "2"});

        DistanceCalculator.FeatureSpec[] specs = specsNumeriques(2);
        boolean changed = k.recomputeCentroid(specs);

        assertTrue(changed);
        String[] centroid = k.getCentroid();
        assertEquals(4.0 / 3.0, Double.parseDouble(centroid[0]), 1e-9);
        assertEquals(4.0 / 3.0, Double.parseDouble(centroid[1]), 1e-9);

        assertFalse(k.recomputeCentroid(specs)); // sense nous membres no hauria de canviar
    }

    /**
     * Recompute amb dimensions nominals ha d'agafar la moda.
     */
    @Test
    public void testRecomputeCentroidNominalMode() {
        Kluster k = new Kluster(new String[]{"A"});
        k.addMember(new String[]{"B"});
        k.addMember(new String[]{"B"});
        k.addMember(new String[]{"A"});

        DistanceCalculator.FeatureSpec[] specs = {DistanceCalculator.FeatureSpec.nominalSingle()};
        boolean changed = k.recomputeCentroid(specs);

        assertTrue(changed);
        assertArrayEquals(new String[]{"B"}, k.getCentroid());
    }

    /**
     * Sense membres el recompute no ha de modificar res ni fallar.
     */
    @Test
    public void testRecomputeSenseMembres() {
        Kluster k = new Kluster(new String[]{"0"});
        assertFalse(k.recomputeCentroid(specsNumeriques(1)));
        assertEquals("0", k.getCentroid()[0]);
    }

    /**
     * Si les especificacions no coincideixen amb les dimensions cal llençar una excepció.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testRecomputeSpecsIncorrectes() {
        Kluster k = new Kluster(new String[]{"0", "0"});
        k.addMember(new String[]{"0", "0"});
        k.recomputeCentroid(specsNumeriques(1));
    }

    /**
     * El mètode toString ha de mostrar centroid i mida actual.
     */
    @Test
    public void testToString() {
        String[] seed = {"1", "2"};
        Kluster k = new Kluster(seed);
        k.addMember(new String[]{"1", "2"});
        k.addMember(new String[]{"3", "4"});

        String expected = "Kluster{centroid=" + Arrays.toString(seed) + ", size=2}";
        assertEquals(expected, k.toString());
    }

}
