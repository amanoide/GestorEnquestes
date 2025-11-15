package edu.upc.prop.clusterxx.domini;

import edu.upc.prop.clusterxx.domini.classes.ClusterEvaluator;
import edu.upc.prop.clusterxx.domini.classes.DistanceCalculator;
import edu.upc.prop.clusterxx.domini.classes.Kluster;

import static org.junit.Assert.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Tests de la classe ClusterEvaluator, seguint l'estil d'Opció i la resta de proves.
 */
public class TestClusterEvaluator {

    private final ClusterEvaluator evaluator = new ClusterEvaluator();

    /** Helper per crear especificacions numèriques. */
    private DistanceCalculator.FeatureSpec[] specs(int dimensions) {
        DistanceCalculator.FeatureSpec[] specs = new DistanceCalculator.FeatureSpec[dimensions];
        Arrays.fill(specs, DistanceCalculator.FeatureSpec.numeric());
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

    @BeforeClass
    public static void iniTests() {
        System.out.println("Iniciant els tests de la classe ClusterEvaluator.\n");
    }

    @AfterClass
    public static void fiTests() {
        System.out.println("Finalitzats els tests de la classe ClusterEvaluator.\n");
    }

    /* -------------------- silhouetteScore -------------------- */

    @Test(expected = IllegalArgumentException.class)
    public void testSilhouetteScoreClustersNull() {
        evaluator.silhouetteScore(null, specs(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSilhouetteScoreClustersBuits() {
        evaluator.silhouetteScore(new ArrayList<>(), specs(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSilhouetteScoreSenseSpecs() {
        evaluator.silhouetteScore(clustersBasics(), null);
    }

    @Test
    public void testSilhouetteScoreUnSolCluster() {
        List<Kluster> clusters = new ArrayList<>();
        clusters.add(cluster(new String[]{"0"}, new String[]{"0"}, new String[]{"1"}));
        assertEquals(0.0, evaluator.silhouetteScore(clusters, specs(1)), 1e-9);
    }

    @Test
    public void testSilhouetteScoreClustersSeparats() {
        List<Kluster> clusters = clustersBasics();
        double score = evaluator.silhouetteScore(clusters, specs(1));
        assertTrue("El silhouette hauria de ser positiu", score > 0.5);
        assertEquals(clusters.size() * 2, clusters.stream().mapToInt(Kluster::size).sum());
    }

    @Test
    public void testSilhouetteScoreClustersSolapats() {
        List<Kluster> clusters = new ArrayList<>();
        clusters.add(cluster(new String[]{"0"}, new String[]{"0"}, new String[]{"5"}));
        clusters.add(cluster(new String[]{"6"}, new String[]{"5"}, new String[]{"6"}));

        double score = evaluator.silhouetteScore(clusters, specs(1));
        assertTrue("Quan hi ha solapament el score ha de ser proper a zero o negatiu", score <= 0.1);
    }

    /* -------------------- silhouettePerCluster -------------------- */

    @Test(expected = IllegalArgumentException.class)
    public void testSilhouettePerClusterNull() {
        evaluator.silhouettePerCluster(null, specs(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSilhouettePerClusterSenseSpecs() {
        evaluator.silhouettePerCluster(clustersBasics(), null);
    }

    @Test
    public void testSilhouettePerClusterBuit() {
        List<Kluster> clusters = new ArrayList<>();
        Kluster buit = new Kluster(new String[]{"0"});
        clusters.add(buit);
        double[] scores = evaluator.silhouettePerCluster(clusters, specs(1));
        assertEquals(1, scores.length);
        assertEquals(0.0, scores[0], 1e-9);
    }

    @Test
    public void testSilhouettePerClusterValors() {
        List<Kluster> clusters = clustersBasics();
        double[] scores = evaluator.silhouettePerCluster(clusters, specs(1));
        assertEquals(2, scores.length);
        assertTrue(scores[0] > 0.5);
        assertTrue(scores[1] > 0.5);
    }
}
