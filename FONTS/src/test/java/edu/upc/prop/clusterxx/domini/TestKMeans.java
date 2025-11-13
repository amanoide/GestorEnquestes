package edu.upc.prop.clusterxx.domini;

import edu.upc.prop.clusterxx.domini.classes.KMeans;

import static org.junit.Assert.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Tests de la classe KMeans.
 */
public class TestKMeans {
    // Per a generar dades de prova amb dos clusters ben definits.
    private List<double[]> dadesDosClusters() {
        List<double[]> dades = new ArrayList<>();

        // Cluster centrat a (0.5, 0.5)
        dades.add(new double[]{0,0});
        dades.add(new double[]{0,1});
        dades.add(new double[]{1,0});
        dades.add(new double[]{1,1});

        // Cluster centrat a (5.5, 5.5)
        dades.add(new double[]{5,5});
        dades.add(new double[]{5,6});
        dades.add(new double[]{6,5});
        dades.add(new double[]{6,6});
        
        return dades;
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
    @Test (expected = IllegalArgumentException.class)
    public void testConstructoraKMeans() {
        new KMeans(0, 2); // k = 0 ha de llençar excepció
    }

    /**
     * Missatge en pantalla de finalització dels tests de la classe KMeans.
     */
    @AfterClass
    public static void fiTestKMeans() {
        System.out.println("Finalitzant els tests de la classe KMeans.");
    }
}   
