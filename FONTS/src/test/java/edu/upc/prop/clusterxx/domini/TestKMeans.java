package edu.upc.prop.clusterxx.domini;

import edu.upc.prop.clusterxx.domini.classes.DistanceCalculator;
import edu.upc.prop.clusterxx.domini.classes.KMeans;

import static org.junit.Assert.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests de la classe KMeans.
 */
public class TestKMeans {
    // Per a generar dades de prova amb dos clusters ben definits.
    private List<String[]> dadesDosClusters() {
        List<String[]> dades = new ArrayList<>();

        // Cluster centrat a (0.5, 0.5)
        dades.add(new String[]{"0", "0"});
        dades.add(new String[]{"0", "1"});
        dades.add(new String[]{"1", "0"});
        dades.add(new String[]{"1", "1"});

        // Cluster centrat a (5.5, 5.5)
        dades.add(new String[]{"5", "5"});
        dades.add(new String[]{"5", "6"});
        dades.add(new String[]{"6", "5"});
        dades.add(new String[]{"6", "6"});

        return dades;
    }

    private DistanceCalculator.FeatureSpec[] specsNumeriques(int dimensions) {
        DistanceCalculator.FeatureSpec[] specs = new DistanceCalculator.FeatureSpec[dimensions];
        for (int i = 0; i < dimensions; ++i) {
            specs[i] = DistanceCalculator.FeatureSpec.numeric(0.0, 10.0);
        }
        return specs;
    }

    /**
     * Missatge en pantalla d'inici dels tests de la classe KMeans.
     */
    @BeforeClass
    public static void iniTestKMeans() {
        System.out.println("Iniciant els tests de la classe KMeans.");
    }

    /**
     * Test constructora KMeans.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFitRebutjaKZero() {
        new KMeans().fit(dadesDosClusters(), 0, 2, specsNumeriques(2));
    }

    /**
     * Missatge en pantalla de finalització dels tests de la classe KMeans.
     */
    @AfterClass
    public static void fiTestKMeans() {
        System.out.println("Finalitzant els tests de la classe KMeans.");
    }
}   
