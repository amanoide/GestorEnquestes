package edu.upc.prop.clusterxx.domini;

import edu.upc.prop.clusterxx.domini.classes.Opcio;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Tests de la classe Opció.
 */
public class TestOpcio {
    /**
     * Tests de la constructora de la classe Opcio.
     * Crea una instància de la classe Opció amb identificador i text i sense ordre. 
     * Comprova que s'han assignat correctament els atributs (identificador i text).
     */
    @Test
    public void testConstructora() {
        Opcio o = new Opcio(1, "Opció A");
        assertEquals(1, o.getId());
        assertEquals("Opció A", o.getText());
        assertNull(o.getOrdre());
        assertFalse(o.esOrdenada());
    }

    /**
     * Tests de la constructora de la classe Opcio amb ordre.
     * Crea una instància de la classe Opció amb identificador, text i ordre.
     * Comprova que s'han assignat correctament els atributs (identificador, text i ordre).
     */
    @Test 
    public void testConstructoraAmbOrdre() {
        Opcio o = new Opcio(1, "Opció A", 3);
        assertEquals(1, o.getId());
        assertEquals("Opció A", o.getText());
        assertEquals(Integer.valueOf(3), o.getOrdre());
        assertTrue(o.esOrdenada());
    }

    /**
     * Tests dels setters de la classe Opcio.
     * Crea una instància de la classe Opció i utilitza els setters per modificar el text i l'ordre.
     * Comprova que els setters tant del text com de l'ordre funcionen correctament.
     */
    @Test 
    public void testSetText() {
        Opcio o = new Opcio(1, "Opció A");
        o.setText("Opció B");
        assertEquals("Opció B", o.getText());
    }

    @Test
    public void testSetOrdre() {
        Opcio o = new Opcio(1, "Opció A");
        assertFalse(o.esOrdenada());
        o.setOrdre(2);
        assertTrue(o.esOrdenada());
        assertEquals(Integer.valueOf(2), o.getOrdre());
    }

    /**
     * Tests del mètode toString de la classe Opcio.
     * Crea dues instàncies de la classe Opció (una sense ordre i una amb ordre) i comprova que el mètode toString retorna la representació correcta.
     */
    @Test
    public void teststoStringSenseOrdre() {
        Opcio o = new Opcio(1, "Text d'opció");
        String expected = "Opcio{id=1, text='Text d'opció'}";
        assertEquals(expected, o.toString());
    }

    @Test
    public void teststoStringAmbOrdre() {
        Opcio o = new Opcio(1, "Text d'opció", 4);
        String expected = "Opcio{id=1, text='Text d'opció', ordre=4}";
        assertEquals(expected, o.toString());
    }

    /**
     * Cas límit d'una opció amb text buit.
     */
    @Test
    public void testOpcioTextBuit(){
        Opcio o = new Opcio(1, "");
        assertEquals("", o.getText());
    }
    
    /**
     * Cas límit d'una opció amb text nul.
     */
    @Test
    public void testOpcioTextNull(){
        Opcio o = new Opcio(1, null);
        assertNull(o.getText());
    }

    /**
     * Cas límit d'una opció amb ordre nul.
     */
    @Test
    public void testOrdreNul() {
        Opcio o = new Opcio(1, "Opció A", 0);
        assertNull(o.getOrdre());
        assertFalse(o.esOrdenada());
    }

    /**
     * Cas límit d'una opció amb ordre negatiu.
     */
    @Test
    public void testOrdreNegatiu() {
        Opcio o = new Opcio(1, "Opció A", -5);
        assertNull(o.getOrdre());
        assertFalse(o.esOrdenada()); 
    } 

}