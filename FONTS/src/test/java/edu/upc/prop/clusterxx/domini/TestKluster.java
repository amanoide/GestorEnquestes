package edu.upc.prop.clusterxx.domini;

import edu.upc.prop.clusterxx.domini.classes.Kluster;

import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Tests de la classe Kluster.
 */
public class TestKluster {
    /**
     * Missatge en pantalla d'inici dels tests de la classe Kluster.
     */
    @BeforeClass
    public static void iniTestKluster() {
        System.out.println("Iniciant els tests de la classe Kluster.");
    }

    /**
     * Test constructora Kluster.
     */
    @Test
    public void testConstructoraKluster() {
        Kluster k = new Kluster(3);
        assertArrayEquals(new double[]{0.0, 0.0, 0.0}, k.getCentroid(), 0.000);
        assertEquals(0,k.size());
        assertNotNull(k.getMembers());
    }

    /**
     * Test constructora per Vector.
     */
    @Test
    public void testConstructoraPerVector() {
        double[] seed = {1.0, 2.0, 3.0};
        Kluster k = new Kluster(seed);
        assertArrayEquals(seed, k.getCentroid(), 0.000);
        assertEquals(0,k.size());
        assertNotNull(k.getMembers());
    }

    /**
     * Test setters Centroid.
     */
    @Test
    public void testSetCentroid() {
        Kluster k = new Kluster(2);
        double[] newCentroid = {4.0, 5.0};
        k.setCentroid(newCentroid);
        assertArrayEquals(newCentroid, k.getCentroid(), 0.000);

        newCentroid[0] = 10.0;
        assertNotEquals(newCentroid[0], k.getCentroid()[0], 0.000); // Comprovar que el centroid no ha canviat

        // Provar amb un altre mida
        k.setCentroid(new double[]{8.0});
        assertEquals(1, k.getCentroid().length);
        assertArrayEquals(new double[]{8.0}, k.getCentroid(), 0.000);
    }

    /**
     * Test getters Centroid.
     */
    @Test
    public void testGetCentroid() {
        double[] seed = {3.0, 4.0, 5.0};
        Kluster k = new Kluster(seed);
        double[] centroid = k.getCentroid();

        centroid[0] = 12.0;
        assertEquals(12.0, k.getCentroid()[0], 0.000);
    }

    /**
     * Test afegir membre.
     */
    @Test
    public void testAddMember() {
        Kluster k = new Kluster(2);
        double[] member1 = {1.0, 2.0};
        double[] member2 = {3.0, 4.0};

        k.addMember(member1);
        k.addMember(member2);

        assertEquals(2, k.size());
        List<double[]> members = k.getMembers();
        assertTrue(members.contains(member1));
        assertTrue(members.contains(member2));
    }

    /**
     * Test getters Members Kluster.
     */
    @Test
    public void testGetMembers() {
        Kluster k = new Kluster(2);
        double[] member = {5.0, 6.0};
        k.addMember(member);

        List<double[]> members = k.getMembers();
        assertEquals(1, members.size());

        members.add(new double[]{7.0, 8.0}); // Modificar la llista retornada
        assertEquals(2, k.size()); 
    
        k.clearMembers();
        assertEquals(0,k.size());
        assertTrue(k.getMembers().isEmpty());
    }

    /**
     * Test recalcular centroid.
     */
    @Test
    public void testRecomputeCentroid() {
        Kluster k = new Kluster(2);
        k.addMember(new double[]{0.0, 0.0});
        k.addMember(new double[]{2.0, 0.0});
        k.addMember(new double[]{0.0, 2.0});
        k.addMember(new double[]{2.0, 2.0});

        // Recalcular centroid
        boolean changed = k.recomputeCentroid(1e-12);
        assertTrue(changed);
        assertArrayEquals(new double[]{1.0, 1.0}, k.getCentroid(), 1e-12);

        // Recalcular amb mateix centroid
        boolean changed2 = k.recomputeCentroid(1e-12);
        assertFalse(changed2);
        assertArrayEquals(new double[]{1.0, 1.0}, k.getCentroid(), 1e-12);
    }

    /** 
     * Test recalcular centroid amb tolerància.
     */
    @Test
    public void testRecomputeCentroidTolerancia() {
        Kluster k = new Kluster(1);
        k.setCentroid((new double[]{0.99}));
        k.addMember(new double[]{1.0});
        k.addMember(new double[]{1.0});
        k.addMember(new double[]{1.0});

        boolean changed = k.recomputeCentroid((0.02));
        assertFalse(changed);
        assertArrayEquals(new double[]{1.0}, k.getCentroid(), 1e-12);

        k.setCentroid((new double[]{0.98}));
        k.addMember(new double[]{1.0});
        boolean changed2 = k.recomputeCentroid((0.005));
        assertTrue(changed2);
        assertArrayEquals(new double[]{1.0}, k.getCentroid(), 1e-12);
    }

    /**
     * Test recalcular centroid amb Kluster buit.
     */
    @Test
    public void testRecomputeCentroidEmpty() {
        Kluster k = new Kluster(2);
        boolean changed = k.recomputeCentroid(1e-12);
        assertFalse(changed);
        assertArrayEquals(new double[]{0.0, 0.0}, k.getCentroid(), 1e-12);
    }

    /**
     * Test toString Kluster.
     */
    @Test
    public void testToString() {
        double[] seed = {1.0, 2.0};
        Kluster k = new Kluster(seed);
        k.addMember(new double[]{3.0, 4.0});
        k.addMember(new double[]{5.0, 6.0});

        String expected = "Kluster{centroid=" + Arrays.toString(seed) + ", size=2}";
        assertEquals(expected, k.toString());
    }

    @AfterClass
    public static void fiTestKluster() {
        System.out.println("Finalitzats els tests de la classe Kluster.");
    }
}
