package edu.upc.prop.clusterxx.domini;

import edu.upc.prop.clusterxx.domini.classes.DistanceCalculator;
import edu.upc.prop.clusterxx.domini.classes.KMeansPlusPlus;
import edu.upc.prop.clusterxx.domini.classes.Kluster;

import static org.junit.Assert.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Tests de la classe KMeansPlusPlus, seguint l'estil dels altres tests de domini.
 */
public class TestKMeansPlusPlus {

    /** Random determinista per controlar la selecció de centroides. */
    private static class ScriptedRandom extends Random {
        private final int[] ints;
        private final double[] doubles;
        private int idxInt;
        private int idxDouble;

        ScriptedRandom(int[] ints, double[] doubles) {
            super(0L);
            this.ints = (ints == null ? new int[0] : ints.clone());
            this.doubles = (doubles == null ? new double[0] : doubles.clone());
        }

        @Override
        public int nextInt(int bound) {
            if (ints.length == 0) return super.nextInt(bound);
            int value = ints[idxInt % ints.length];
            idxInt++;
            return Math.floorMod(value, bound);
        }

        @Override
        public double nextDouble() {
            if (doubles.length == 0) return super.nextDouble();
            double value = doubles[idxDouble % doubles.length];
            idxDouble++;
            return value;
        }
    }

    /** Helper per crear especificacions numèriques. */
    private DistanceCalculator.FeatureSpec[] specsNumeriques(int dim) {
        DistanceCalculator.FeatureSpec[] specs = new DistanceCalculator.FeatureSpec[dim];
        for (int i = 0; i < dim; i++) specs[i] = DistanceCalculator.FeatureSpec.numeric();
        return specs;
    }

    /** Dades amb dos clústers clarament separats. */
    private List<String[]> dadesClustersSeparats() {
        List<String[]> dades = new ArrayList<>();
        for (int i = 0; i < 4; i++) dades.add(new String[]{"0", "0"});
        for (int i = 0; i < 4; i++) dades.add(new String[]{"10", "10"});
        return dades;
    }

    @BeforeClass
    public static void iniTestKMeansPlusPlus() {
        System.out.println("Iniciant els tests de la classe KMeansPlusPlus.\n");
    }

    @AfterClass
    public static void fiTestKMeansPlusPlus() {
        System.out.println("Finalitzats els tests de la classe KMeansPlusPlus.\n");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInitCentroidsDataNull() {
        new KMeansPlusPlus().initCentroids(null, 2, specsNumeriques(2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInitCentroidsDataBuida() {
        new KMeansPlusPlus().initCentroids(new ArrayList<>(), 1, specsNumeriques(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInitCentroidsKAmbZero() {
        List<String[]> dades = dadesClustersSeparats();
        new KMeansPlusPlus().initCentroids(dades, 0, specsNumeriques(2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInitCentroidsKMassaGran() {
        List<String[]> dades = dadesClustersSeparats();
        new KMeansPlusPlus().initCentroids(dades, dades.size() + 1, specsNumeriques(2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInitCentroidsSenseSpecs() {
        List<String[]> dades = dadesClustersSeparats();
        new KMeansPlusPlus().initCentroids(dades, 2, null);
    }

    @Test
    public void testInitCentroidsDeterminista() {
        List<String[]> dades = dadesClustersSeparats();
        ScriptedRandom rnd = new ScriptedRandom(new int[]{0}, new double[]{0.0});
        KMeansPlusPlus kpp = new KMeansPlusPlus(rnd);

        List<String[]> centroides = kpp.initCentroids(dades, 2, specsNumeriques(2));

        assertEquals(2, centroides.size());
        assertArrayEquals(new String[]{"0", "0"}, centroides.get(0));
        assertArrayEquals(new String[]{"10", "10"}, centroides.get(1));
    }

    @Test
    public void testFitAmbClustersSeparats() {
        List<String[]> dades = dadesClustersSeparats();
        // primer centroid data[0], segon centroid força cap al segon grup
        ScriptedRandom rnd = new ScriptedRandom(new int[]{0}, new double[]{0.0});
        KMeansPlusPlus kpp = new KMeansPlusPlus(rnd);
        DistanceCalculator.FeatureSpec[] specs = specsNumeriques(2);

        List<Kluster> clusters = kpp.fit(dades, 2, 20, specs);

        assertEquals(2, clusters.size());
        int totalMembers = clusters.stream().mapToInt(Kluster::size).sum();
        assertEquals(dades.size(), totalMembers);

        boolean zero = false;
        boolean deu = false;
        for (Kluster cluster : clusters) {
            String[] centroid = cluster.getCentroid();
            if ("0".equals(centroid[0]) && "0".equals(centroid[1])) {
                zero = true;
                assertEquals(4, cluster.size());
            } else if ("10".equals(centroid[0]) && "10".equals(centroid[1])) {
                deu = true;
                assertEquals(4, cluster.size());
            }
        }
        assertTrue(zero);
        assertTrue(deu);
    }
}
