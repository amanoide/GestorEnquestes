package edu.upc.prop.clusterxx.domini;

import edu.upc.prop.clusterxx.domini.classes.Enquesta;
import edu.upc.prop.clusterxx.domini.classes.Pregunta;
import edu.upc.prop.clusterxx.domini.classes.Usuari;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests de la classe Enquesta.
 */
public class TestEnquesta {
    // Per a construir l'usuari creador de l'enquesta.
    private Usuari creador() {
        return new Usuari("creador", "contrasenya");
    }

    // Per crear preguntes de text lliure de l'enquesta.
    private Pregunta pText(String id, String text) {
        return new Pregunta(id, text);
    }
    

    // Per crear preguntes numèriques de l'enquesta.
    private Pregunta pNum(String id, String text, double min, double max) {
        return new Pregunta(id, text, min, max);
    }

    /* 
    // Per crear preguntes qualitatives de l'enquesta.
    private Pregunta pQual(String id, String text, TipusPregunta tipus, int maxSel) {
        return new Pregunta(id, text, tipus, maxSel);
    } */

    Enquesta e;
    Usuari u;

    /**
     * Reutilitza la mateixa enquesta i creador abans de cada prova per simplificar les assertions.
     */
    @Before
    public void abansDeCadaTestEnquesta() {
        u = creador();
        e = new Enquesta("E1","Títol de l'enquesta", "Descripció de l'enquesta", u);
    }

    /**
     * Test constructora Enquesta.
     */
    @Test
    public void testConstructoraEnquesta() {
        assertEquals("E1", e.getId());
        assertEquals("Títol de l'enquesta", e.getTitol());
        assertEquals("Descripció de l'enquesta", e.getDescripcio());
        assertEquals(u.getUsername(), e.getIdCreador());
        
        assertTrue(e.getPreguntes().isEmpty());             // Enquesta sense preguntes inicialment
        assertEquals(0, e.getNumParticipants()); 
        assertTrue(e.getParticipants().isEmpty());          // Cap participant de l'enquesta inicialment
    }   

    /**
     * Tests setter i getter del títol.
     */
    @Test
    public void testSetGetTitol() {
        e.setTitol("Nou títol");
        assertEquals("Nou títol", e.getTitol());
    }

    /**
     * Tests setter i getter de la descripció.
     */
    @Test
    public void testSetGetDescripcio() {
        e.setDescripcio("Nova descripció");
        assertEquals("Nova descripció", e.getDescripcio());
    }

    /*  ===========================================
             TESTS PREGUNTES CLASSE ENQUESTA
        =========================================== */
    /**
     * Test afegir i recuperar preguntes d'una enquesta.
     */
    @Test
    public void testAfegirRecuperarPreguntes() {
        Pregunta p1 = pText("P1", "Quin és el teu nom?");
        Pregunta p2 = pNum("P2", "Quants anys tens?", 0, 120);

        e.afegirPregunta(p1);
        e.afegirPregunta(p2);

        assertEquals(2, e.getPreguntes().size());
        assertSame(p1, e.getPregunta("P1"));
        assertSame(p2, e.getPregunta("P2"));
    }

    /**
     * Tests eliminar Pregunta.
     */
    @Test
    public void testEliminarPregunta() {
        Pregunta p1 = pText("P1", "Quin és el teu nom?");
        Pregunta p2 = pNum("P2", "Quants anys tens?", 0, 120);

        e.afegirPregunta(p1);
        e.afegirPregunta(p2);

        e.eliminarPregunta("P1");
        assertEquals(1, e.getPreguntes().size());
        assertNull(e.getPregunta("P1"));

        assertSame(p2, e.getPregunta("P2"));
    }

    /**
     * Tests afegir pregunta amb mateix id.
     */
    @Test
    public void testAfegirPreguntaMateixID() {
        Pregunta p1 = pText("P1", "Quin és el teu nom?");
        Pregunta p2 = pNum("P1", "Quants anys tens?", 0, 120); // Mateix ID que p1

        e.afegirPregunta(p1);
        e.afegirPregunta(p2); // Ha de substituir p1

        assertEquals(1, e.getPreguntes().size());
        assertSame(p2, e.getPregunta("P1"));
        assertNotSame(p1, e.getPregunta("P1"));
    }

    /**
     * Tests modificar pregunta existent.
     */
    @Test
    public void testModificarPregunta() {
        Pregunta p1 = pText("P1", "Quin és el teu nom?");
        e.afegirPregunta(p1);

        Pregunta pModificada = pText("P1", "Com et dius?");
        e.modificarPregunta("P1", pModificada);

        assertEquals(1, e.getPreguntes().size());
        assertEquals("Com et dius?", e.getPregunta("P1").getText());
    }

    /**
     * Tests modificar pregunta no existent.
     */
    @Test
    public void testModificarPreguntaNoExistent() {
        Pregunta pModificada = pText("P1", "Com et dius?");
        
        e.modificarPregunta("P1", pModificada);
        assertTrue(e.getPreguntes().isEmpty()); // No s'ha afegit cap pregunta
    }

    /**
     * Tests obtenir llistat preguntes i modificar-les no afecta a les preguntes pròpies de l'enquesta.
     */
    @Test
    public void testGetPreguntes() {
        // Enquesta e = new Enquesta("E1","Títol de l'enquesta", "Descripció de l'enquesta", creador());
        Pregunta p1 = pText("P1", "Quin és el teu nom?");
        e.afegirPregunta(p1);

        List<Pregunta> llistatPreguntes = e.getPreguntes();
        llistatPreguntes.clear();

        // Comprovem que la pregunta dins de l'enquesta no s'ha modificat
        assertEquals("Quin és el teu nom?", e.getPregunta("P1").getText());
        assertEquals(1, e.getPreguntes().size());
        assertNotNull(e.getPreguntes());
    }

    /**
     * No es pot afegir una pregunta nul·la; ha de llençar NullPointerException.
     */
    @Test(expected = NullPointerException.class)
    public void testAfegirPreguntaNullLlençaExcepcio() {
        e.afegirPregunta(null);
    }

    /**
     * Eliminar una pregunta inexistent no ha de modificar la col·lecció de preguntes.
     */
    @Test
    public void testEliminarPreguntaInexistentNoAfecta() {
        Pregunta p1 = pText("P1", "Pregunta de prova");
        e.afegirPregunta(p1);

        e.eliminarPregunta("INEXISTENT");

        assertEquals(1, e.getPreguntes().size());
        assertSame(p1, e.getPregunta("P1"));
    }
    
    /*  ===========================================
            TESTS PARTICIPANTS CLASSE ENQUESTA
        =========================================== */

    /**
     * Tests registrar participació d'un usuari en una enquesta.
     */
    @Test
    public void testRegistrarParticipacio() {

        assertFalse(e.haRespostUsuari("usuari1"));
        e.registrarParticipacio("usuari1");
        assertTrue(e.haRespostUsuari("usuari1"));
        assertEquals(1, e.getNumParticipants());    

        e.registrarParticipacio("usuari1"); // El duplicat no hauria de comptar
        assertEquals(1, e.getNumParticipants());

        assertFalse(e.haRespostUsuari("NoParticipant")); // Usuari no participant 
    }

    /**
     * Tests obtenir llistat de participants d'una enquesta.
     */
    @Test
    public void testGetParticipants() {
 

        e.registrarParticipacio("usuari1");
        e.registrarParticipacio("usuari2");

        List<String> participants = e.getParticipants();
        participants.clear(); // No hauria de afectar a l'enquesta
        List<String> participantsPost = e.getParticipants();
        assertEquals(2, participantsPost.size());
        assertTrue(participantsPost.contains("usuari1"));
        assertTrue(participantsPost.contains("usuari2"));
    }
    
}
