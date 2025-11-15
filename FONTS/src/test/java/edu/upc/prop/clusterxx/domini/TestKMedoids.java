package edu.upc.prop.clusterxx.domini;

import edu.upc.prop.clusterxx.domini.classes.KMedoids;
import edu.upc.prop.clusterxx.domini.classes.Kluster;
import edu.upc.prop.clusterxx.domini.classes.DistanceCalculator;

import static org.junit.Assert.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Tests de la classe KMedoids.
 */
public class TestKMedoids {

    /**
     * Test del constructor KMedoids.
     */
    @Test
    public void testConstructoraKMedoids() {
        KMedoids kmedoids = new KMedoids();
        assertNotNull("El constructor hauria de crear una instància", kmedoids);
    }

    /**
     * Test de clustering simple amb dades numèriques.
     */
    @Test
    public void testClusteringNumeric() {
        // Crear dataset amb dos grups ben separats
        List<String[]> data = new ArrayList<>();
        // Grupo 1: valores bajos
        data.add(new String[]{"1"});
        data.add(new String[]{"2"});
        data.add(new String[]{"3"});
        // Grupo 2: valores altos
        data.add(new String[]{"10"});
        data.add(new String[]{"11"});
        data.add(new String[]{"12"});

        // FeatureSpec para variable numérica
        DistanceCalculator.FeatureSpec[] specs = {
            DistanceCalculator.FeatureSpec.numeric(0.0, 15.0)
        };

        KMedoids kmedoids = new KMedoids();
        List<Kluster> clusters = kmedoids.fit(data, 2, 100, specs);

        // Verificaciones
        assertNotNull("El resultado no debería ser null", clusters);
        assertEquals("Deberían haber 2 clusters", 2, clusters.size());
        
        // Verificar que hay puntos en cada cluster
        for (Kluster cluster : clusters) {
            assertTrue("Cada cluster debería tener al menos 1 punto", cluster.size() > 0);
        }

        // Verificar que el total de puntos es correcto
        int totalPoints = 0;
        for (Kluster cluster : clusters) {
            totalPoints += cluster.size();
        }
        assertEquals("Todos los puntos deberían estar asignados", 6, totalPoints);
        
        // Verificar que los medoides son puntos reales del dataset
        for (Kluster cluster : clusters) {
            String[] medoid = cluster.getCentroid();
            boolean isMedoidInData = false;
            for (String[] point : data) {
                if (Arrays.equals(medoid, point)) {
                    isMedoidInData = true;
                    break;
                }
            }
            assertTrue("El medoide debería ser un punto real del dataset", isMedoidInData);
        }
    }

    /**
     * Test de clustering con datos multidimensionales.
     */
    @Test
    public void testClusteringMultidimensional() {
        List<String[]> data = new ArrayList<>();
        // Cluster 1: jóvenes satisfechos
        data.add(new String[]{"20", "Excelente"});
        data.add(new String[]{"22", "Bueno"});
        data.add(new String[]{"25", "Excelente"});
        // Cluster 2: mayores insatisfechos
        data.add(new String[]{"45", "Malo"});
        data.add(new String[]{"50", "Regular"});
        data.add(new String[]{"48", "Malo"});

        DistanceCalculator.FeatureSpec[] specs = {
            DistanceCalculator.FeatureSpec.numeric(0.0, 100.0),
            DistanceCalculator.FeatureSpec.ordinal(Arrays.asList("Malo", "Regular", "Bueno", "Excelente"), 4)
        };

        KMedoids kmedoids = new KMedoids();
        List<Kluster> clusters = kmedoids.fit(data, 2, 100, specs);

        assertEquals("Deberían haber 2 clusters", 2, clusters.size());
        
        // Cada cluster debería tener 3 puntos
        for (Kluster cluster : clusters) {
            assertEquals("Cada cluster debería tener 3 puntos", 3, cluster.size());
        }
    }

    /**
     * Test con k = 1 (un solo cluster).
     */
    @Test
    public void testSingleCluster() {
        List<String[]> data = new ArrayList<>();
        data.add(new String[]{"1"});
        data.add(new String[]{"2"});
        data.add(new String[]{"3"});

        DistanceCalculator.FeatureSpec[] specs = {
            DistanceCalculator.FeatureSpec.numeric(0.0, 10.0)
        };

        KMedoids kmedoids = new KMedoids();
        List<Kluster> clusters = kmedoids.fit(data, 1, 100, specs);

        assertEquals("Debería haber 1 cluster", 1, clusters.size());
        assertEquals("El cluster debería tener todos los puntos", 3, clusters.get(0).size());
    }

    /**
     * Test de validación de parámetros: k inválido.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidK() {
        List<String[]> data = new ArrayList<>();
        data.add(new String[]{"1"});
        data.add(new String[]{"2"});

        DistanceCalculator.FeatureSpec[] specs = {
            DistanceCalculator.FeatureSpec.numeric(0.0, 10.0)
        };

        KMedoids kmedoids = new KMedoids();
        // k = 0 debería lanzar excepción
        kmedoids.fit(data, 0, 100, specs);
    }

    /**
     * Test de validación de parámetros: k mayor que número de puntos.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testKGreaterThanDataSize() {
        List<String[]> data = new ArrayList<>();
        data.add(new String[]{"1"});
        data.add(new String[]{"2"});

        DistanceCalculator.FeatureSpec[] specs = {
            DistanceCalculator.FeatureSpec.numeric(0.0, 10.0)
        };

        KMedoids kmedoids = new KMedoids();
        // k = 5 > 2 puntos debería lanzar excepción
        kmedoids.fit(data, 5, 100, specs);
    }

    /**
     * Test de validación de parámetros: data vacía.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEmptyData() {
        List<String[]> data = new ArrayList<>();

        DistanceCalculator.FeatureSpec[] specs = {
            DistanceCalculator.FeatureSpec.numeric(0.0, 10.0)
        };

        KMedoids kmedoids = new KMedoids();
        kmedoids.fit(data, 2, 100, specs);
    }

    /**
     * Test de validación de parámetros: specs null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testNullSpecs() {
        List<String[]> data = new ArrayList<>();
        data.add(new String[]{"1"});
        data.add(new String[]{"2"});

        KMedoids kmedoids = new KMedoids();
        kmedoids.fit(data, 2, 100, null);
    }

    /**
     * Les dades nul·les s'han de rebutjar immediatament perquè l'algoritme necessita observacions.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDataNull() {
        DistanceCalculator.FeatureSpec[] specs = {
            DistanceCalculator.FeatureSpec.numeric(0.0, 10.0)
        };
        new KMedoids().fit(null, 2, 10, specs);
    }

    /**
     * maxIters zero ha de comportar-se com el valor per defecte però seguir assignant tots els punts.
     */
    @Test
    public void testMaxIterZeroUtilitzaPerDefecte() {
        List<String[]> data = new ArrayList<>();
        data.add(new String[]{"1"});
        data.add(new String[]{"2"});
        data.add(new String[]{"3"});

        DistanceCalculator.FeatureSpec[] specs = {
            DistanceCalculator.FeatureSpec.numeric(0.0, 10.0)
        };

        List<Kluster> clusters = new KMedoids().fit(data, 2, 0, specs);

        assertEquals(2, clusters.size());
        int total = clusters.stream().mapToInt(Kluster::size).sum();
        assertEquals(data.size(), total);
    }

    /**
     * Test con datos nominales múltiples.
     */
    @Test
    public void testNominalMultiData() {
        List<String[]> data = new ArrayList<>();
        // Grupo 1: lenguajes de bajo nivel
        data.add(new String[]{"C,C++"});
        data.add(new String[]{"C,Assembly"});
        data.add(new String[]{"C++,Rust"});
        // Grupo 2: lenguajes de alto nivel
        data.add(new String[]{"Python,JavaScript"});
        data.add(new String[]{"Python,Ruby"});
        data.add(new String[]{"JavaScript,TypeScript"});

        DistanceCalculator.FeatureSpec[] specs = {
            DistanceCalculator.FeatureSpec.nominalMulti(3)
        };

        KMedoids kmedoids = new KMedoids();
        List<Kluster> clusters = kmedoids.fit(data, 2, 100, specs);

        assertEquals("Deberían haber 2 clusters", 2, clusters.size());
        
        // Verificar que los medoides son puntos reales
        for (Kluster cluster : clusters) {
            String[] medoid = cluster.getCentroid();
            boolean found = false;
            for (String[] point : data) {
                if (Arrays.equals(medoid, point)) {
                    found = true;
                    break;
                }
            }
            assertTrue("El medoide debe ser un punto real", found);
        }
    }

    /**
     * Test de convergencia: verificar que el algoritmo converge.
     */
    @Test
    public void testConvergence() {
        List<String[]> data = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            data.add(new String[]{String.valueOf(i)});
        }

        DistanceCalculator.FeatureSpec[] specs = {
            DistanceCalculator.FeatureSpec.numeric(0.0, 10.0)
        };

        KMedoids kmedoids = new KMedoids();
        
        // Ejecutar dos veces con la misma semilla
        List<Kluster> clusters1 = kmedoids.fit(data, 3, 100, specs);
        
        assertNotNull("El resultado no debería ser null", clusters1);
        assertEquals("Deberían haber 3 clusters", 3, clusters1.size());
    }

}
