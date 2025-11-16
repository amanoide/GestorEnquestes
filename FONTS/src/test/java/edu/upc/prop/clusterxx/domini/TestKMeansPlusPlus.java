package edu.upc.prop.clusterxx.domini;

import edu.upc.prop.clusterxx.domini.classes.DistanceCalculator;
import edu.upc.prop.clusterxx.domini.classes.KMeansPlusPlus;
import edu.upc.prop.clusterxx.domini.classes.Kluster;

import static org.junit.Assert.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Tests de la classe KMeansPlusPlus.
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
        for (int i = 0; i < dim; i++) specs[i] = DistanceCalculator.FeatureSpec.numeric(0.0, 10.0);
        return specs;
    }

    /** Dades amb dos clústers clarament separats. */
    private List<String[]> dadesClustersSeparats() {
        List<String[]> dades = new ArrayList<>();
        for (int i = 0; i < 4; i++) dades.add(new String[]{"0", "0"});
        for (int i = 0; i < 4; i++) dades.add(new String[]{"10", "10"});
        return dades;
    }

    /**
     * initCentroids ha de rebutjar conjunts de dades nuls.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitCentroidsDataNull() {
        new KMeansPlusPlus().initCentroids(null, 2, specsNumeriques(2));
    }

    /**
     * Tampoc no es pot inicialitzar amb llistes buides de punts.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitCentroidsDataBuida() {
        new KMeansPlusPlus().initCentroids(new ArrayList<>(), 1, specsNumeriques(1));
    }

    /**
     * Una k zero ha de llençar excepció perquè com a mínim cal un clúster.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitCentroidsKAmbZero() {
        List<String[]> dades = dadesClustersSeparats();
        new KMeansPlusPlus().initCentroids(dades, 0, specsNumeriques(2));
    }

    /**
     * k no pot superar el nombre de vectors disponibles.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitCentroidsKMassaGran() {
        List<String[]> dades = dadesClustersSeparats();
        new KMeansPlusPlus().initCentroids(dades, dades.size() + 1, specsNumeriques(2));
    }

    /**
     * Les especificacions són obligatòries per mesurar les distàncies durant la inicialització.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitCentroidsSenseSpecs() {
        List<String[]> dades = dadesClustersSeparats();
        new KMeansPlusPlus().initCentroids(dades, 2, null);
    }

    /**
     * Amb un Random determinista s'han de seleccionar consistentment els centroides dels dos grups.
     */
    @Test
    public void testInitCentroidsDeterminista() {
        List<String[]> dades = dadesClustersSeparats();
        ScriptedRandom rnd = new ScriptedRandom(new int[]{0}, new double[]{0.99});
        KMeansPlusPlus kpp = new KMeansPlusPlus(rnd);

        List<String[]> centroides = kpp.initCentroids(dades, 2, specsNumeriques(2));

        assertEquals(2, centroides.size());
        assertArrayEquals(new String[]{"0", "0"}, centroides.get(0));
        assertArrayEquals(new String[]{"10", "10"}, centroides.get(1));
    }

    /**
     * La combinació d'inicialització i fit ha de reconstruir els dos clústers separats.
     */
    @Test
    public void testFitAmbClustersSeparats() {
        List<String[]> dades = dadesClustersSeparats();
        // primer centroid data[0], segon centroid força cap al segon grup
        ScriptedRandom rnd = new ScriptedRandom(new int[]{0}, new double[]{0.99});
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
            double c0 = Double.parseDouble(centroid[0]);
            double c1 = Double.parseDouble(centroid[1]);
            if (Math.abs(c0) < 1e-9 && Math.abs(c1) < 1e-9) {
                zero = true;
                assertEquals(4, cluster.size());
            } else if (Math.abs(c0 - 10.0) < 1e-9 && Math.abs(c1 - 10.0) < 1e-9) {
                deu = true;
                assertEquals(4, cluster.size());
            }
        }
        assertTrue(zero);
        assertTrue(deu);
    }
}
