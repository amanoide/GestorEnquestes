package edu.upc.prop.clusterxx.domini;

import edu.upc.prop.clusterxx.domini.classes.Resposta;
import edu.upc.prop.clusterxx.domini.classes.Usuari;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests de la classe Resposta.
 */
public class TestResposta {
    Usuari u;
    Resposta r;

    // Codi d'inicialització abans de cada test 
    @Before
    public void abansDeCadaTestResposta() {
        u =  new Usuari("Usuari1", "contrasenya");
        r = new Resposta("R1", "P1", "Resposta A", u);
    }

    /**
     * Tests de la constructora de la classe Resposta.
     * Crea una instància de la classe Resposta amb identificador i text. 
     * Comprova que s'han assignat correctament els atributs (identificador i text).
     */
    @Test
    public void testConstructoraResposta() {
        Usuari u1 = new Usuari("usuari1", "contrasenya1");
        Resposta r1 = new Resposta("R1", "P1.1", "Resposta A", u1);
        assertEquals("P1.1", r1.getIdPregunta());
        assertEquals("Resposta A", r1.getTextResposta());
    }

    /**
     * Tests del mètode modificarResposta de la classe Resposta.
     */
    @Test
    public void testModificarResposta() {
        r.modificarResposta("Resposta B");
        assertEquals("Resposta B", r.getTextResposta());
    }

    /**
     * Tests del mètode toString de la classe Resposta.
     */
    @Test
    public void testToStringResposta() {
        String expected = "Resposta{id='R1', idPregunta='P1', textResposta='Resposta A', usuari='Usuari1'}";
        assertEquals(expected, r.toString());
    }

    /**
     * Casos límit.
     */

    @Test
    public void testToStringAmbTextNull() {
        r.modificarResposta(null);
        String expected = "Resposta{id='R1', idPregunta='P1', textResposta='null', usuari='Usuari1'}";
        assertEquals(expected, r.toString());
    }

    @Test
    public void testTextRespostaBuit() {
        r.modificarResposta("");
        assertEquals("", r.getTextResposta());
    }

    @Test
    public void testTextAmbEspais() {
        r.modificarResposta(" resposta amb espais ");
        assertEquals(" resposta amb espais ", r.getTextResposta());
    }

}
