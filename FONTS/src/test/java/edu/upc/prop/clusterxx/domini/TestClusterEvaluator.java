package edu.upc.prop.clusterxx.domini;

import edu.upc.prop.clusterxx.domini.classes.ClusterEvaluator;
import edu.upc.prop.clusterxx.domini.classes.DistanceCalculator;
import edu.upc.prop.clusterxx.domini.classes.Kluster;

import static org.junit.Assert.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests de la classe ClusterEvaluator.
 */
public class TestClusterEvaluator {

    private final ClusterEvaluator evaluator = new ClusterEvaluator();

    /** Helper per crear especificacions numèriques. 
    */
    private DistanceCalculator.FeatureSpec[] specs(int dimensions) {
        DistanceCalculator.FeatureSpec[] specs = new DistanceCalculator.FeatureSpec[dimensions];
        for (int i = 0; i < dimensions; i++) {
            specs[i] = DistanceCalculator.FeatureSpec.numeric(0.0, 10.0);
        }
        return specs;
    }

    /** Crea un klúster amb centroid donat i membres addicionals. */
    private Kluster cluster(String[] centroid, String[]... members) {
        Kluster k = new Kluster(centroid);
        for (String[] member : members) {
            k.addMember(member);
        }
        return k;
    }

    /** Conjunt de clusters simples per validar càlculs. */
    private List<Kluster> clustersBasics() {
        List<Kluster> clusters = new ArrayList<>();
        clusters.add(cluster(new String[]{"0"}, new String[]{"0"}, new String[]{"0"}));
        clusters.add(cluster(new String[]{"10"}, new String[]{"10"}, new String[]{"10"}));
        return clusters;
    }

        /*  =============================================
            TESTS silhouetteScore CLASSE ClusterEvaluator
        ================================================= */

    /**
     * No es pot calcular el silhouette si la llista de klústers és nul·la.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSilhouetteScoreClustersNull() {
        evaluator.silhouetteScore(null, specs(1));
    }

    /**
     * També s'ha de rebutjar una llista buida de klústers.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSilhouetteScoreClustersBuits() {
        evaluator.silhouetteScore(new ArrayList<>(), specs(1));
    }

    /**
     * Les especificacions de distància són obligatòries per normalitzar les dimensions.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSilhouetteScoreSenseSpecs() {
        evaluator.silhouetteScore(clustersBasics(), null);
    }

    /**
     * Amb un únic clúster el silhouette global ha de ser zero.
     */
    @Test
    public void testSilhouetteScoreUnSolCluster() {
        List<Kluster> clusters = new ArrayList<>();
        clusters.add(cluster(new String[]{"0"}, new String[]{"0"}, new String[]{"1"}));
        assertEquals(0.0, evaluator.silhouetteScore(clusters, specs(1)), 1e-9);
    }

    /**
     * Klústers ben separats haurien de donar un silhouette positiu alt.
     */
    @Test
    public void testSilhouetteScoreClustersSeparats() {
        List<Kluster> clusters = clustersBasics();
        double score = evaluator.silhouetteScore(clusters, specs(1));
        assertTrue("El silhouette hauria de ser positiu", score > 0.5);
        assertEquals(clusters.size() * 2, clusters.stream().mapToInt(Kluster::size).sum());
    }

    /**
     * Quan hi ha solapament important el silhouette s'ha d'aproximar a zero o ser negatiu.
     */
    @Test
    public void testSilhouetteScoreClustersSolapats() {
        List<Kluster> clusters = new ArrayList<>();
        clusters.add(cluster(new String[]{"0"}, new String[]{"0"}, new String[]{"5"}));
        clusters.add(cluster(new String[]{"6"}, new String[]{"5"}, new String[]{"6"}));

        double overlapped = evaluator.silhouetteScore(clusters, specs(1));
        double separated = evaluator.silhouetteScore(clustersBasics(), specs(1));
        assertTrue("Quan hi ha solapament el score ha de ser inferior al dels clusters separats", overlapped < separated);
    }

        /*  =================================================
            TESTS silhouettePerCluster CLASSE ClusterEvaluator
        ===================================================== */

    /**
     * La versió per clúster també ha de validar que la llista no sigui nul·la.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSilhouettePerClusterNull() {
        evaluator.silhouettePerCluster(null, specs(1));
    }

    /**
     * I cal comprovar que les especificacions no siguin nul·les.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSilhouettePerClusterSenseSpecs() {
        evaluator.silhouettePerCluster(clustersBasics(), null);
    }

    /**
     * Els clústers buits han de retornar un silhouette zero per no introduir soroll.
     */
    @Test
    public void testSilhouettePerClusterBuit() {
        List<Kluster> clusters = new ArrayList<>();
        Kluster buit = new Kluster(new String[]{"0"});
        clusters.add(buit);
        double[] scores = evaluator.silhouettePerCluster(clusters, specs(1));
        assertEquals(1, scores.length);
        assertEquals(0.0, scores[0], 1e-9);
    }

    /**
     * En el cas ideal cada clúster hauria de tenir un silhouette per sobre de 0.5.
     */
    @Test
    public void testSilhouettePerClusterValors() {
        List<Kluster> clusters = clustersBasics();
        double[] scores = evaluator.silhouettePerCluster(clusters, specs(1));
        assertEquals(2, scores.length);
        assertTrue(scores[0] > 0.5);
        assertTrue(scores[1] > 0.5);
    }
}
